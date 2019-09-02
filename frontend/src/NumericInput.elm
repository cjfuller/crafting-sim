module NumericInput exposing (..)

import Css exposing (..)
import Html.Styled exposing (Html, div, input, label, text)
import Html.Styled.Attributes exposing (css, for, id, name, type_, value)
import Html.Styled.Events exposing (onInput)
import SharedStyles exposing (..)
import Util exposing (capitalize)


heightPx =
    40


filledHeightPx =
    25


field fieldName fieldMin fieldMax fieldValue action styles =
    input
        [ type_ "number"
        , id fieldName
        , name fieldName
        , Html.Styled.Attributes.min fieldMin
        , Html.Styled.Attributes.max fieldMax
        , onInput action
        , css styles
        ]
        [ text fieldValue ]


fieldNoValueStyles =
    [ minHeight (px heightPx)
    , border3 zero none (hex "#fff")
    , borderTopLeftRadius (px 3)
    , borderTopRightRadius (px 3)
    , backgroundColor colors.veryLightGrey
    , width (pct 100)
    , paddingLeft paddingAmt
    , boxSizing borderBox
    , property "-webkit-appearance" "none"
    , property "-moz-appearance" "textfield"
    , borderBottom3 (px 1) solid colors.darkButton
    , hover
        [ important (property "-webkit-appearance" "none")
        , important (property "-moz-appearance" "textfield")
        , important (cursor text_)
        ]
    ]


fieldWithValueStyles =
    [ minHeight (px heightPx)
    , paddingTop (px (heightPx - filledHeightPx))
    , backgroundColor colors.veryLightGrey
    , width (pct 100)
    , paddingLeft paddingAmt
    , boxSizing borderBox
    , border3 zero none (hex "#fff")
    , property "-webkit-appearance" "none"
    , property "-moz-appearance" "textfield"
    , property "-webkit-appearance" "none"
    , property "-moz-appearance" "textfield"
    , fontFamilies [ qt "Source Sans Pro", "sans-serif" ]
    , fontSize (px 16)
    , borderBottom3 (px 1) solid colors.darkButton
    , hover
        [ important (property "-webkit-appearance" "none")
        , important (property "-moz-appearance" "textfield")
        , important (cursor text_)
        ]
    , invalid
        [ important (border3 zero none (hex "#fff"))
        , important (borderBottom3 (px 1) solid (hex "#f00"))
        ]
    ]


labelNoValueStyles =
    [ float left
    , paddingLeft paddingAmt
    , boxSizing borderBox
    , color colors.disabled
    , position absolute
    , top (px (heightPx / 4))
    , pointerEvents none
    , hover
        [ cursor text_
        ]
    ]


labelWithValueStyles =
    [ float left
    , paddingLeft paddingAmt
    , boxSizing borderBox
    , color colors.lightButton
    , fontSize (px 8)
    , position absolute
    , top (px (heightPx / 8))
    , pointerEvents none
    , hover
        [ cursor text_
        ]
    ]


numberField : String -> String -> String -> String -> (String -> msg) -> Html msg
numberField fieldName fieldMin fieldMax fieldValue action =
    div
        [ css
            [ width (px 300)
            , minHeight (px heightPx)
            , height (px heightPx)
            , display block
            , marginTop paddingAmt
            , backgroundColor colors.veryLightGrey
            , boxSizing borderBox
            , borderTopLeftRadius (px 3)
            , borderTopRightRadius (px 3)
            , position relative
            ]
        ]
        [ label
            [ for fieldName
            , css
                (if fieldValue == "" then
                    labelNoValueStyles

                 else
                    labelWithValueStyles
                )
            ]
            [ text (capitalize fieldName) ]
        , field fieldName
            fieldMin
            fieldMax
            fieldValue
            action
            (if fieldValue == "" then
                fieldNoValueStyles

             else
                fieldWithValueStyles
            )
        ]
