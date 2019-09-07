module Main exposing (Msg(..), aboutView, classSelector, classes, crossClassAbilities, crossClassSelector, document, getStat, init, itemSelector, main, maybeFetchItems, navbar, onUrlChange, onUrlRequest, optimizerView, stats, statsSelector, subscriptions, update, updateStatsIn, view)

import Browser exposing (Document, UrlRequest)
import Browser.Navigation exposing (Key)
import Card exposing (card)
import CheckboxInput exposing (checkbox)
import Css exposing (..)
import Dict exposing (Dict)
import Html
import Html.Styled exposing (..)
import Html.Styled.Attributes exposing (attribute, checked, class, css, for, href, id, name, tabindex, type_, value)
import Html.Styled.Events exposing (onClick, onInput)
import Http
import JsonUtil exposing (encodeModel, itemsDecoder)
import Loading exposing (defaultConfig)
import Model exposing (CrafterStats, Model, Page(..))
import NumericInput exposing (numberField)
import Radio exposing (radio)
import SharedStyles exposing (..)
import Struts exposing (..)
import Url exposing (Url)


type Msg
    = ClassSelected String
    | StatsEdited CrafterStats
    | LoadedItems (List String)
    | ItemSelected String
    | Placeholder
    | UpdateOutput String
    | GoOptimize
    | OptimizeComplete String
    | NavigateTo UrlRequest
    | NavigatedTo Page
    | AddAbility String
    | RemoveAbility String


main =
    Browser.application
        { init = init
        , view = document
        , update = update
        , subscriptions = subscriptions
        , onUrlRequest = onUrlRequest
        , onUrlChange = onUrlChange
        }


init : () -> Url -> Key -> ( Model, Cmd Msg )
init _ url key =
    ( { cls = ""
      , crossClass = []
      , items = Dict.empty
      , stats =
            { level = Nothing
            , craftsmanship = Nothing
            , control = Nothing
            , cp = Nothing
            }
      , item = ""
      , key = key
      , output = ""
      , waiting = False
      , page =
            if url.path == "/about" then
                About

            else
                Optimizer
      }
    , Cmd.none
    )


document : Model -> Document Msg
document model =
    { title = "FF14 crafting optimizer"
    , body = [ (view >> toUnstyled) model ]
    }


optimizerView : Model -> List (Html Msg)
optimizerView model =
    [ div
        []
        [ classSelector model
        , vStrut spacingAmt
        , statsSelector model
        , vStrut spacingAmt
        , itemSelector model
        , vStrut spacingAmt
        , crossClassSelector model
        , vStrut spacingAmt
        , div
            [ css
                [ displayFlex
                , alignItems center
                ]
            ]
            [ button
                [ onClick GoOptimize
                , Html.Styled.Attributes.disabled model.waiting
                , css
                    [ minHeight (px 40)
                    , minWidth (px 150)
                    , fontSize (px 16)
                    , fontFamilies [ qt "Source Sans Pro", "sans-serif" ]
                    , backgroundColor
                        (if model.waiting then
                            colors.disabled

                         else
                            colors.darkButton
                        )
                    , color (hex "#fff")
                    , border3 zero none (hex "#fff")
                    , borderRadius (px 3)
                    , boxShadow4 zero (px 3) (px 3) colors.shadow
                    , hover
                        [ cursor pointer
                        ]
                    , focus
                        [ backgroundColor (blend colors.darkButton (hex "#fff"))
                        ]
                    ]
                ]
                [ text "Optimize" ]
            , hStrut paddingAmt
            , fromUnstyled
                (Loading.render Loading.Spinner
                    { defaultConfig | color = hexColors.lightButton, size = 17 }
                    (if model.waiting then
                        Loading.On

                     else
                        Loading.Off
                    )
                )
            ]
        ]
    , hStrut spacingAmt
    , card "Output"
        [ div
            [ css
                [ borderTop3 (px 1) solid colors.lightGrey
                , marginTop paddingAmt
                , minWidth (px 500)
                , minHeight (vh 30)
                , maxHeight (vh 90)
                , fontFamilies [ qt "IBM Plex Mono", .value monospace ]
                , fontSize (px 14)
                , whiteSpace preWrap
                , overflowY auto
                ]
            ]
            [ text model.output ]
        ]
        []
    ]


aboutView : Model -> List (Html Msg)
aboutView model =
    [ card "About"
        [ text """
    This crafting optimizer is intended to help with leveling and max-level
    crafting in Final Fantasy 14 Shadowbringers. You input your class, stats,
    and the item you want to craft, and it will optimize a series of crafting
    abilities, trying to maximize quality while achieving a 100% success
    rate. It will then generate a macro or series of macros to execute these
    actions.
    """
        , vStrut spacingAmt
        , text """Note that because the intended output target is a macro, this won't
    generate anything that has to react to item condition or other procs, and
    therefore you may be able to get better results by tuning the craft
    manually.
    """
        , vStrut spacingAmt
        , text """
    The underlying optimization algorithm uses a method that includes
    randomness, so you may get different result from multiple tries. If you
    don't like the macro you got for some reason, try again!
    """
        , vStrut spacingAmt
        , text "Found a bug? "
        , br [] []
        , text "File an issue on the"
        , span [] [ text "\u{00A0}" ]
        , a
            [ href "https://github.com/cjfuller/crafting-sim/issues"
            , css [ styles.link ]
            ]
            [ text "GitHub issues page." ]
        , vStrut spacingAmt
        , text "Interested in the status of other classes / features? Want to help?"
        , br [] []
        , text "Check out the"
        , span [] [ text "\u{00A0}" ]
        , a
            [ href "https://github.com/cjfuller/crafting-sim"
            , css [ styles.link ]
            ]
            [ text "project on GitHub" ]
        ]
        []
    ]


view : Model -> Html Msg
view model =
    div [ css [ height (calc (vh 100) minus navbarHeight), width (pct 100), backgroundColor colors.veryLightGrey ] ]
        [ navbar model
        , div [ css [ styles.pageContainer ] ]
            (case model.page of
                Optimizer ->
                    optimizerView model

                About ->
                    aboutView model
            )
        ]


navbar : Model -> Html Msg
navbar model =
    div [ css [ styles.navBar ] ]
        [ div [] [ text "FF14 Crafting Optimizer" ]
        , growingHStrut
        , a
            [ href "/"
            , css
                ([ styles.navLink ]
                    ++ (if model.page == Optimizer then
                            [ styles.selectedLink ]

                        else
                            []
                       )
                )
            ]
            [ text "Optimizer" ]
        , hStrut spacingAmt
        , a
            [ href "/about"
            , css
                ([ styles.navLink ]
                    ++ (if model.page == About then
                            [ styles.selectedLink ]

                        else
                            []
                       )
                )
            ]
            [ text "About" ]
        , hStrut paddingAmt
        ]


maybeFetchItems : Model -> String -> Cmd Msg
maybeFetchItems m cls =
    case Dict.get cls m.items of
        Just _ ->
            Cmd.none

        Nothing ->
            Http.get
                { url = "/api/v0/items/" ++ cls
                , expect =
                    Http.expectJson
                        (\r -> LoadedItems (Result.withDefault [] r))
                        itemsDecoder
                }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Placeholder ->
            ( model, Cmd.none )

        ClassSelected cls ->
            ( { model | cls = cls }, maybeFetchItems model cls )

        LoadedItems items ->
            ( { model | items = Dict.insert model.cls items model.items }, Cmd.none )

        StatsEdited s ->
            ( { model | stats = s }, Cmd.none )

        ItemSelected s ->
            ( { model | item = s }, Cmd.none )

        UpdateOutput s ->
            ( { model | output = s }, Cmd.none )

        GoOptimize ->
            ( { model | waiting = True }
            , Http.post
                { url = "/api/v0/optimize"
                , body = Http.jsonBody (encodeModel model)
                , expect =
                    Http.expectString
                        (\r ->
                            case r of
                                Ok s ->
                                    OptimizeComplete s

                                Err e ->
                                    OptimizeComplete "Server error."
                        )
                }
            )

        OptimizeComplete s ->
            ( { model | waiting = False, output = s }, Cmd.none )

        NavigatedTo p ->
            ( { model | page = p }, Cmd.none )

        NavigateTo ur ->
            case ur of
                Browser.Internal url ->
                    ( model, Browser.Navigation.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Browser.Navigation.load href )

        AddAbility a ->
            ( { model | crossClass = model.crossClass ++ [ a ] }, Cmd.none )

        RemoveAbility a ->
            ( { model | crossClass = List.filter (\x -> x /= a) model.crossClass }, Cmd.none )


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none


onUrlRequest : UrlRequest -> Msg
onUrlRequest req =
    NavigateTo req


onUrlChange : Url -> Msg
onUrlChange u =
    case u.path of
        "/about" ->
            NavigatedTo About

        _ ->
            NavigatedTo Optimizer


classes =
    [ "weaver", "culinarian", "carpenter", "alchemist", "armorer", "blacksmith", "goldsmith", "leatherworker" ]


crossClassAbilities =
    [ "Careful Synthesis"
    , "Careful Synthesis II"
    , "Hasty Touch"
    , "Reclaim"
    , "Muscle Memory"
    ]


stats =
    [ { name = "level", min = "1", max = "80" }
    , { name = "craftsmanship", min = "0", max = "999999" }
    , { name = "control", min = "1", max = "999999" }
    , { name = "CP", min = "1", max = "999999" }
    ]


classSelector : Model -> Html Msg
classSelector model =
    card "Crafting class"
        (List.map
            (\cls -> radio cls model.cls (ClassSelected cls))
            classes
        )
        []


updateStatsIn : CrafterStats -> String -> Maybe Int -> CrafterStats
updateStatsIn st name val =
    case val of
        Nothing ->
            st

        Just i ->
            case name of
                "craftsmanship" ->
                    { st | craftsmanship = val }

                "control" ->
                    { st | control = val }

                "CP" ->
                    { st | cp = val }

                "level" ->
                    { st | level = val }

                _ ->
                    st


getStat : CrafterStats -> String -> String
getStat st name =
    case name of
        "craftsmanship" ->
            Maybe.withDefault "" (Maybe.map String.fromInt st.craftsmanship)

        "control" ->
            Maybe.withDefault "" (Maybe.map String.fromInt st.control)

        "CP" ->
            Maybe.withDefault "" (Maybe.map String.fromInt st.cp)

        "level" ->
            Maybe.withDefault "" (Maybe.map String.fromInt st.level)

        _ ->
            ""


statsSelector : Model -> Html Msg
statsSelector model =
    card "Crafter stats"
        (List.map
            (\stat ->
                numberField stat.name
                    stat.min
                    stat.max
                    (getStat model.stats stat.name)
                    (\si ->
                        StatsEdited
                            (updateStatsIn model.stats stat.name (String.toInt si))
                    )
            )
            stats
        )
        []


itemSelector : Model -> Html Msg
itemSelector model =
    card "Item to craft"
        [ select
            [ name "item"
            , css
                [ width (pct 100)
                , maxWidth (px 300)
                , important (fontFamilies [ qt "Source Sans Pro", "sans-serif" ])
                , important (fontSize (px 16))
                , verticalAlign middle
                ]
            , Html.Styled.Attributes.required True
            , onInput (\s -> ItemSelected s)
            ]
            ([ option
                [ value "" ]
                [ text "Select an item..." ]
             ]
                ++ List.map (\i -> option [ value i ] [ text i ])
                    (Maybe.withDefault [] (Dict.get model.cls model.items))
            )
        ]
        []


crossClassSelector : Model -> Html Msg
crossClassSelector model =
    card "Cross-class abilities"
        (List.map
            (\a ->
                let
                    checked =
                        List.member a model.crossClass
                in
                checkbox a
                    checked
                    (if checked then
                        RemoveAbility a

                     else
                        AddAbility a
                    )
            )
            crossClassAbilities
        )
        []
