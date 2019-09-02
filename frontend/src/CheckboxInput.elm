module CheckboxInput exposing (checkbox)

import Css exposing (..)
import Html.Styled exposing (Html, div, i, text)
import Html.Styled.Attributes exposing (class, css)
import Html.Styled.Events exposing (onClick)
import SharedStyles exposing (..)
import Struts exposing (..)



-- TODO(colin): accessibility markers for the checkbox


checkbox : String -> Bool -> msg -> Html msg
checkbox name checked action =
    div [ css [ displayFlex, alignItems center, marginTop paddingAmt, hover [ cursor pointer ] ], onClick action ]
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
            ]
            (if checked then
                [ i [ class "material-icons", css [ fontSize (px 14), color (hex "#fff") ] ] [ text "done" ]
                ]

             else
                []
            )
        , hStrut paddingAmt
        , text name
        ]
