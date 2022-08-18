# BlumBox.quark

The Blumlein Box (aka BlumBox) is a collection of stereo imaging transforms and analysespackaged together as a SuperCollider Quark. 

## Credits
Developed by Emmett Miller with the support of Dr. Joseph Anderson, whose paper Classical Stereo Imaging Transforms--A Review serves as the basis for this project, and [DXARTS](https://dxarts.washington.edu/) at the [University of Washington](https://uw.edu/).

## Installing

### Requirements
BlumBox.quark was developed on SuperCollider 3.12.2. Though previous versions may be compatible, it might not be a bad idea to install the latest SuperCollider update.

To install the quark, run the following in SuperCollider:
```supercollider
Quarks.install("https://github.com/emillwe/BlumBox.quark.git");
```

## The Transforms
The Blumlein Box implements the following 9 stereo transforms.

### MS to LR
Transforms a signal from the MS domain to the LR domain

### LR to MS
Transforms a signal from the LR domain to the LS domain

### Rotation
Rotates a stereo signal, preserving relative positions in the stereo field

### Width
Widens or narrows a stereo signal

### Balance
Stereo panning as implemented on most consumer electronics--compresses a stereo signal towards the L or R channel

### Middle Panorama
Rotates the M-axis of a stereo image, leaving the S-axis in place

### Asymmetry
Rotates the S-axis of a stereo image, leaving the M-axis in place

### Left Panorama
Rotates only the R-axis of a stereo image

### Right Panorama
Rotates only the L-axis of a stereo image

## The Analyses
The Blumlein Box also includes the following 5 imaging analyses.

### Power

### Balance

### Correlation

### Angle

### Radius