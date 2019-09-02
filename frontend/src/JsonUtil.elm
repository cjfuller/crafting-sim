module JsonUtil exposing (encodeModel, itemsDecoder)

import Json.Decode as D
import Json.Encode as E
import Model exposing (CrafterStats, Model)


num0 : Maybe Int -> E.Value
num0 v =
    E.int (Maybe.withDefault 0 v)


encodeCrafterStats : CrafterStats -> E.Value
encodeCrafterStats s =
    E.object
        [ ( "level", num0 s.level )
        , ( "craftsmanship", num0 s.craftsmanship )
        , ( "control", num0 s.control )
        , ( "cp", num0 s.cp )
        ]


encodeModel : Model -> E.Value
encodeModel m =
    E.object
        [ ( "cls", E.string m.cls )
        , ( "stats", encodeCrafterStats m.stats )
        , ( "item", E.string m.item )
        ]


itemsDecoder : D.Decoder (List String)
itemsDecoder =
    D.list D.string
