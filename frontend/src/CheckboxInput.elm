module CheckboxInput exposing (checkbox)

import Css exposing (..)
import Html.Styled exposing (Html, div, i, text)
import Html.Styled.Attributes exposing (attribute, class, css, id, tabindex)
import Html.Styled.Events exposing (on, onClick)
import Json.Decode
import SharedStyles exposing (..)
import Struts exposing (..)


sanitize : String -> String
sanitize =
    String.replace " " "_"


checkbox : String -> Bool -> msg -> Html msg
checkbox name checked action =
    div
        [ css
            [ displayFlex
            , alignItems center
            , marginTop paddingAmt
            , hover [ cursor pointer ]
            ]
        , onClick action
        , on "keypress" (Json.Decode.succeed action)
        ]
        [ div
            [ css
                ([ width (px 14)
                 , height (px 14)
                 , boxSizing borderBox
                 , borderRadius (px 3)
                 , displayFlex
                 , alignItems center
                 , justifyContent center
                 , hover
                    [ cursor pointer
                    ]
                 ]
                    ++ (if checked then
                            [ backgroundColor colors.lightButton
                            ]

                        else
                            [ border3 (px 2) solid colors.lightGrey
                            ]
                       )
                )
            , tabindex 0
            , attribute "role" "checkbox"
            , attribute "aria-labelledby" ("check_label_" ++ sanitize name)
            , attribute "aria-checked"
                (if checked then
                    "true"

                 else
                    "false"
                )
            ]
            (if checked then
                [ i [ class "material-icons", css [ fontSize (px 14), color (hex "#fff") ] ] [ text "done" ]
                ]

             else
                []
            )
        , hStrut paddingAmt
        , div [ id ("check_label_" ++ sanitize name) ] [ text name ]
        ]
