module Model exposing (CrafterStats, Model, Page(..))

import Browser.Navigation exposing (Key)
import Dict exposing (Dict)


type alias CrafterStats =
    { level : Maybe Int
    , craftsmanship : Maybe Int
    , control : Maybe Int
    , cp : Maybe Int
    }


type Page
    = Optimizer
    | About


type alias Model =
    { cls : String
    , crossClass : List String
    , items : Dict String (List String)
    , stats : CrafterStats
    , item : String
    , key : Key
    , output : String
    , page : Page
    , waiting : Bool
    }
