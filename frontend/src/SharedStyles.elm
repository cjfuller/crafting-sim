module SharedStyles exposing (..)

import Css exposing (..)


navbarHeight =
    px 40


paddingAmtPx =
    10


paddingAmt =
    px paddingAmtPx


spacingAmtPx =
    20


spacingAmt =
    px spacingAmtPx


hexColors =
    { nav = "#7994b1"
    , darkButton = "#13293d"
    , lightButton = "#2a628f"
    , link = "#4c5c6e"
    , shadow = "#ccc"
    , disabled = "#999"
    , lightGrey = "#eaeaea"
    , veryLightGrey = "#fafafa"
    }


colors =
    { nav = hex hexColors.nav
    , darkButton = hex hexColors.darkButton
    , lightButton = hex hexColors.lightButton
    , link = hex hexColors.link
    , shadow = hex hexColors.shadow
    , disabled = hex hexColors.disabled
    , lightGrey = hex hexColors.lightGrey
    , veryLightGrey = hex hexColors.veryLightGrey
    }


blend : Color -> Color -> Color
blend c0 c1 =
    rgba
        ((c0.red + c1.red) // 2)
        ((c0.green + c1.green) // 2)
        ((c0.blue + c1.blue) // 2)
        ((c0.alpha + c1.alpha) / 2)


styles =
    { navBar =
        Css.batch
            [ displayFlex
            , alignItems center
            , position fixed
            , top zero
            , width (vw 100)
            , boxSizing borderBox
            , height navbarHeight
            , paddingLeft paddingAmt
            , paddingRight paddingAmt
            , backgroundColor colors.nav
            , boxShadow4 zero (px 3) (px 3) colors.shadow
            ]
    , pageContainer =
        Css.batch
            [ margin2 navbarHeight auto
            , padding spacingAmt
            , displayFlex
            , maxWidth (px 900)
            , justifyContent spaceBetween
            , backgroundColor colors.veryLightGrey
            ]
    , link =
        Css.batch
            [ textDecoration none
            , color colors.link
            , boxSizing borderBox
            ]
    , navLink =
        Css.batch
            [ textDecoration none
            , color inherit
            , boxSizing borderBox
            , height (pct 100)
            , paddingTop paddingAmt
            ]
    , selectedLink = Css.batch [ borderBottom3 (px 3) solid (hex "#000") ]
    }
