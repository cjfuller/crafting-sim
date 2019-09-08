module Radio exposing (radio)

import Css exposing (..)
import Dict exposing (Dict)
import Html
import Html.Styled exposing (..)
import Html.Styled.Attributes exposing (attribute, css, id, tabindex)
import Html.Styled.Events exposing (onClick, onInput)
import SharedStyles exposing (..)
import Struts exposing (..)


radio : String -> String -> msg -> Html msg
radio item selectedItem action =
    div
        [ css
            [ displayFlex
            , alignItems center
            , paddingLeft paddingAmt
            , paddingTop paddingAmt
            , hover
                [ cursor pointer
                ]
            ]
        , onClick action
        ]
        [ div
            [ css
                [ border3 (px 2) solid colors.lightButton
                , borderRadius (pct 50)
                , height (px 14)
                , width (px 14)
                , hover
                    [ backgroundColor (hex "#eee")
                    ]
                , displayFlex
                , alignItems center
                , justifyContent center
                ]
            , tabindex 0
            , attribute "role" "radio"
            , attribute "aria-labelledby" ("radio_label_" ++ item)
            , attribute "aria-checked"
                (if selectedItem == item then
                    "true"

                 else
                    "false"
                )
            ]
            (if selectedItem == item then
                [ div
                    [ css
                        [ height (px 10)
                        , width (px 10)
                        , borderRadius (pct 50)
                        , backgroundColor colors.lightButton
                        ]
                    ]
                    []
                ]

             else
                []
            )
        , hStrut paddingAmt
        , div [ id ("radio_label_" ++ item), css [] ] [ text item ]
        ]
