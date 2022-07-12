// TODO: Copyright (GPLv3.0?), topic scentence, summary, attribution


//---------------------------------------------------------------------
//	TODO: BlumBox in a scentence
//
// 	Classes: BlumUGen, BlumLRtoMS, BlumMStoLR, BlumRotate, BlumWidth, BlumBalance,
//           BlumMPanMS, BlumAsymMS, BlumRPan, BlumLPan
//
//	TODO: BlumBox Summary
//
//---------------------------------------------------------------------

/*
~-- Attribution --~
The implementation of all the following stereo imaging transforms are taken
from "Classic Stereo Imaging Transforms--A Review", by Dr. Joseph Anderson
@ DXARTS, University of Washington.
*/

BlumUGen {
	*makeStereo { |in|
		// ensure input has 2 channels
		(in.numChannels != 2).if {
			// ^NumChannels.ar(
			// 	// input: in,
			// 	input: 2.sqrt.reciprocal * in,
			// 	numChannels: 2,
			// 	mixdown: true
			// );
			^((2.sqrt.reciprocal * in).dup(2))
		}
    }
}

//-----------------------------------------------------------------------
// convert LR signal to MS domain

/*
///// args /////
in: LR input signal

///// returns ////
MS signal
*/

BlumLRtoMS : BlumUGen {
	*ar { |in|
		var mid, side;
		var sum, diff;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		mid = 2.sqrt.reciprocal * sum;
		side = 2.sqrt.reciprocal * diff;

		^[mid, side];
	}

	*kr { |in|
		var mid, side;
		var sum, diff;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		mid = 2.sqrt.reciprocal * sum;
		side = 2.sqrt.reciprocal * diff;

		^[mid, side];

	}
}

//-----------------------------------------------------------------------
// convert MS signal to LR domain

/*
///// args /////
in: MS input signal

///// returns ////
LR signal
*/

BlumMStoLR : BlumUGen {
	*ar { |in|
		var left, right;
		var sum, diff;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		left = 2.sqrt.reciprocal * sum;
		right = 2.sqrt.reciprocal * diff;

		^[left, right];
	}

	*kr { |in|
		var left, right;
		var sum, diff;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		left = 2.sqrt.reciprocal * sum;
		right = 2.sqrt.reciprocal * diff;

		^[left, right];

	}
}

// TODO: Sin-Cos pan, panMS

//-----------------------------------------------------------------------
// wrapper for Rotate2, using stereo input and degree arguments

/*
///// args /////
in: stereo input signal
angle: rotation angle in degrees, from perspective along x-axis. Limits [-180, 180]

///// returns ////
stereo rotated signal

NOTE: BlumRotate will take non-stereo inputs and mix them to stereo.
For a mono signal, BlumRotate acts as a simple panner.
*/

BlumRotate : BlumUGen {
	*ar { |in, angle|
		// TODO: wrap angle inside bounds [+= 180]
		// e.g. while angle >= 360, angle %= 360 . . .

		// normalize degrees for Rotate2 pos argument
		var pos = angle / -180.0;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		^Rotate2.ar(in[0], in[1], pos);
	}

	*kr { |in, angle|
		// TODO: wrap angle inside bounds
		var pos = angle / -180.0;

		// make input stereo
		in = BlumUGen.makeStereo(in);

		^Rotate2.kr(in[0], in[1], pos);
	}
}

//-----------------------------------------------------------------------
// rotate L and R channels towards or away the M-axis,
// narrowing or widening the stereo image

/*
///// args /////
in: stereo input signal
angle: width angle in degrees, from perspective along x-axis. Limits [-45, 45]
TODO: Check angle limits

///// returns ////
stereo widened/narrowed signal
*/

BlumWidth : BlumUGen {
	*ar { |in, angle|
		var widthL, widthR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		widthL = (in[0] * rads.cos) - (in[1] * rads.sin);
		widthR = (in[0] * rads.sin).neg + (in[1] * rads.cos);

		^[widthL, widthR]
	}

	*kr { |in, angle|
		var widthL, widthR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		widthL = (in[0] * rads.cos) - (in[1] * rads.sin);
		widthR = (in[0] * rads.sin).neg + (in[1] * rads.cos);

		^[widthL, widthR]
	}
}

//-----------------------------------------------------------------------
// TODO: Better class description
// rotate M and S channels towards or away the L-axis

/*
///// args /////
in: stereo input signal
angle: balance angle in degrees, from perspective along x-axis. Limits [-45, 45]
TODO: Check angle limits

///// returns ////
stereo balanced signal
*/

BlumBalance : BlumUGen {
	*ar { |in, angle|
		var balL, balR;
		var rads;

		// TODO: wrap angle?

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		balL = in[0] * 2.sqrt * ((45 - angle).degrad).cos;
		balR = in[1] * 2.sqrt * ((45 - angle).degrad).sin;

		^[balL, balR]
	}

	*kr { |in, angle|
		var balL, balR;
		var rads;

		// TODO: wrap angle?

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		balL = in[0] * 2.sqrt * ((45 - angle).degrad).cos;
		balR = in[1] * 2.sqrt * ((45 - angle).degrad).sin;

		^[balL, balR]
	}
}

//-----------------------------------------------------------------------
// rotate only the M-axis, keeping the S-axis in place
/*
///// args /////
in: MS-domain signal in the format [M, S]
angle: mPan angle in degrees, from perspective along x-axis. Limits [-45, 45]
TODO: Check angle limits

///// returns ////
M-panned MS signal in the format [M, S]

TODO: include BlumMPanLR? name just BlumMPan?
*/

BlumMPanMS : BlumUGen {
	*ar { |in, angle|
		var mPanM, mPanS;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		mPanM = in[0] * rads.cos;
		mPanS = in[0] * rads.sin + in[1];

		^[mPanM, mPanS]
	}

	*kr { |in, angle|
		var mPanM, mPanS;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		// TODO: transform to LR and back?
		in = BlumUGen.makeStereo(in);

		mPanM = in[0] * rads.cos;
		mPanS = in[0] * rads.sin + in[1];

		^[mPanM, mPanS]
	}
}

//-----------------------------------------------------------------------
// rotate only the S-axis, keeping the M-axis in place
/*
///// args /////
in: MS-domain signal in the format [M, S]
angle: asymmetry angle in degrees, from perspective along x-axis. Limits [-45, 45]
TODO: Check angle limits

///// returns ////
Asymmetry-transformed MS signal in the format [M, S]

TODO: include BlumMPanLR? name just BlumMPan?
*/

BlumAsymMS : BlumUGen {
	*ar { |in, angle|
		var asymM, asymS;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		// TODO: transform to LR and back?
		in = BlumUGen.makeStereo(in);

		asymM = in[0] - (in[1] * rads.sin);
		asymS = in[1] * rads.cos;

		^[asymM, asymS];
	}

	*kr { |in, angle|
		var asymM, asymS;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		// TODO: transform to LR and back?
		in = BlumUGen.makeStereo(in);

		asymM = in[0] - (in[1] * rads.sin);
		asymS = in[1] * rads.cos;

		^[asymM, asymS];
	}
}

//-----------------------------------------------------------------------
// rotate only the R-axis, keeping the L-axis in place

/*
///// args /////
in: stereo signal
angle: R-pan angle in degrees, from perspective along x-axis.
TODO: Check angle limits

///// returns ////
R-panned stereo signal
*/
BlumRPan : BlumUGen {
	*ar { |in, angle|
		var rPanL, rPanR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		rPanL = in[0] + (in[1] * rads.sin);
		rPanR = in[1] * rads.cos;

		^[rPanL, rPanR]
	}

	*kr { |in, angle|
		var rPanL, rPanR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		rPanL = in[0] + (in[1] * rads.sin);
		rPanR = in[1] * rads.cos;

		^[rPanL, rPanR]
	}
}

//-----------------------------------------------------------------------
// rotate only the L-axis, keeping the R-axis in place

/*
///// args /////
in: stereo signal
angle: L-pan angle in degrees, from perspective along x-axis.
TODO: Check angle limits

///// returns ////
L-panned stereo signal
*/

BlumLPan : BlumUGen {
	*ar { |in, angle|
		var lPanL, lPanR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		lPanL = in[0] * rads.cos;
		lPanR = (in[0] * rads.sin).neg + in[1];
		^[lPanL, lPanR]
	}

	*kr { |in, angle|
		var lPanL, lPanR;
		var rads;

		// TODO: wrap angle?
		rads = angle.degrad;

		// TODO: should this be able to accept a mono signal?
		// Throw warning?
		// make input stereo
		in = BlumUGen.makeStereo(in);

		lPanL = in[0] * rads.cos;
		lPanR = (in[0] * rads.sin).neg + in[1];
		^[lPanL, lPanR]
	}
}
