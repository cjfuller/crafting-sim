module Util exposing (..)


capitalize : String -> String
capitalize s =
    String.concat
        [ String.left 1 s |> String.toUpper
        , String.dropLeft 1 s
        ]
