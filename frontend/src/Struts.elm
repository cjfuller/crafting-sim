module Struts exposing (..)

import Css exposing (..)
import Html.Styled exposing (div)
import Html.Styled.Attributes exposing (css)


vStrut h =
    div [ css [ height h ] ] []


hStrut w =
    div [ css [ width w ] ] []


growingHStrut =
    div [ css [ flexGrow (num 1) ] ] []
