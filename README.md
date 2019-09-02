# A crafting optimizer for FF14

This tool helps you generate an optimized crafting macro for an item in FF14,
given your crafter's stats. The goal is to maximize quality of an item while
keeping the chance of success at 100%.

## Usage:
Download the latest release from the releases tab. Be sure to grab the
correct one for windows or mac/linux. (You must have java installed, but you
probably already do.)

Run from the command line to print usage instructions:
```
$ crafting-sim
```

Here's a complete usage example:
```
$ crafting-sim --craftsmanship=1434 --control=1328 --level=73 --cp=477 "Bright Linen Yarn"
```

This will optimize for a while (and notify you of progress along the way), and eventually write out output like:
```
Best result is:
Steady Hand II
Inner Quiet
Basic Touch
Focused Touch
Careful Synthesis
Steady Hand II
Master's Mend
Careful Synthesis II
Brand of the Elements
Prudent Touch
Hasty Touch II
Master's Mend
Steady Hand II
Prudent Touch
Advanced Touch
Prudent Touch
Prudent Touch
Prudent Touch
Careful Synthesis II
 with expected quality 8478.0 and success rate 1.0


macro 1

/ac "Steady Hand II" <wait.3>
/ac "Inner Quiet" <wait.3>
/ac "Basic Touch" <wait.3>
/ac "Focused Touch" <wait.3>
/ac "Careful Synthesis" <wait.3>
/ac "Steady Hand II" <wait.3>
/ac "Master's Mend" <wait.3>
/ac "Careful Synthesis II" <wait.3>
/ac "Brand of the Elements" <wait.3>
/ac "Prudent Touch" <wait.3>
/ac "Hasty Touch II" <wait.3>
/ac "Master's Mend" <wait.3>
/ac "Steady Hand II" <wait.3>
/ac "Prudent Touch" <wait.3>
/echo Macro 1 complete <se.7>



macro 2

/ac "Advanced Touch" <wait.3>
/ac "Prudent Touch" <wait.3>
/ac "Prudent Touch" <wait.3>
/ac "Prudent Touch" <wait.3>
/ac "Careful Synthesis II" <wait.3>
/echo Macro 2 complete <se.7>
```

You can then copy these macros into your ff14 client and run them to craft
the item. The reported expected quality is an average of 20 simulations of
crafting the item.

## Implementation progress

This tool is very new and lots of things are not yet implemented. If there's
something missing you want added, file an issue! (See below.)

- [ ] Alchemist
- [ ] Armorer
- [ ] Blacksmith
- [ ] Carpenter
- [x] Culinarian
- [ ] Goldsmith
- [ ] Leatherworker
- [x] Weaver

### Known limitations (besides unimplemented classes)

The simulations don't take into account item condition at present.

The intended output is a macro, so anything that requires reacting to
condition is not included in the possible macros.

Specializations are not implemented.

Cross-class abilities are not implemented.

## Reporting an issue

If you find an issue, please file an issue on the github repository.

If the issue is that an item was not crafted as expected (notably if progress
didn't get to 100%), it would be helpful to include the full command you ran
to run the simulation (including all your stats and which item it was), as
well as the output, including the list of abilities and the expected quality
/ success rate.

## Thanks

The FF14 crafting model code was based heavily on the excellent
https://github.com/doxxx/ffxiv-craft-opt-web, another crafting
optimizer tool.
