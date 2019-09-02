module Card exposing (card)

import Css exposing (..)
import Html.Styled exposing (Html, div, text)
import Html.Styled.Attributes exposing (css)
import SharedStyles exposing (..)


card : String -> List (Html msg) -> List Style -> Html msg
card title contents extraStyles =
    div
        [ css
            [ backgroundColor (hex "#fff")
            , boxSizing borderBox
            , boxShadow4 zero (px 3) (px 3) colors.shadow
            , padding paddingAmt
            , borderRadius (px 3)
            ]
        ]
        ([ div [ css [ fontWeight bold ] ]
            [ text title
            ]
         ]
            ++ contents
        )
