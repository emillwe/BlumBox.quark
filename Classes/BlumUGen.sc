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
	*confirmStereoInputs { |in|
		var inNumChannels;
		var stereoNumChannels = 2;  // stereo will be 2

		inNumChannels = in.numChannels;
		if(inNumChannels != stereoNumChannels, {
			Error(
				"[BlumUGen] In number of channels (%) does not match expected numInputs (%).".format(inNumChannels, stereoNumChannels)
			).errorString.postln;
			this.halt
		});

		^inNumChannels
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
		var left, right;
		var sum, diff;
		var mid, side;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		sum = left + right;
		diff = left - right;

		mid = 2.sqrt.reciprocal * sum;
		side = 2.sqrt.reciprocal * diff;

		^[mid, side];
	}

	*kr { |in|
		var left, right;
		var sum, diff;
		var mid, side;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		sum = left + right;
		diff = left - right;

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

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		#left, right = in;

		sum = left + right;
		diff = left - right;

		left = 2.sqrt.reciprocal * sum;
		right = 2.sqrt.reciprocal * diff;

		^[left, right];
	}

	*kr { |in|
		var left, right;
		var sum, diff;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		#left, right = in;

		sum = left + right;
		diff = left - right;

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
angle: rotation angle in radians, from perspective along x-axis. Wraps around [-pi, pi]

///// returns ////
stereo rotated signal

NOTE: BlumRotate will take non-stereo inputs and mix them to stereo.
For a mono signal, BlumRotate acts as a simple panner.
*/

BlumRotate : BlumUGen {
	*ar { |in, angle|
		var left, right;

		// TODO: check for degrees input?

		// normalize radians for Rotate2 pos argument
		var pos = angle / -pi;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		^Rotate2.ar(left, right, pos);
	}

	// MS input/output
	*arMS {|in, angle|
		var inM, inS;
		var rotM, rotS;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		#inM, inS = in;

		rotM = (angle.cos * inM) - (angle.sin * inS);
		rotS = (angle.cos * inM) + (angle.sin * inS);

		^[rotM, rotS];
	}

	*kr { |in, angle|
		var left, right;

		// normalize radians for Rotate2 pos argument
		var pos = angle / -pi;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		^Rotate2.kr(left, right, pos);
	}
}

//-----------------------------------------------------------------------
// rotate L and R channels towards or away the M-axis,
// narrowing or widening the stereo image

/*
///// args /////
in: stereo input signal
angle: width angle in radians, from perspective along x-axis. Limits [-pi/4, pi/4]

///// returns /////
stereo widened/narrowed signal
*/

BlumWidth : BlumUGen {
	*ar { |in, angle|
		var left, right;
		var widthL, widthR;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		widthL = (angle.cos * left) - (angle.sin * right);
		widthR = (angle.sin.neg * left) + (angle.cos * right);

		^[widthL, widthR]
	}

	// MS input/output
	*arMS {|in, angle|
		var inM, inS;
		var widthM, widthS;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		#inM, inS = in;

		widthM = 2.sqrt * ((pi/4) - angle).sin * inM;
		widthS = 2.sqrt * ((pi/4) - angle).cos * inS;

		^[widthM, widthS];
	}

	*kr { |in, angle|
		var left, right;
		var widthL, widthR;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		widthL = (angle.cos * left) - (angle.sin * right);
		widthR = (angle.sin * left).neg + (angle.cos * right);

		^[widthL, widthR]
	}
}

//-----------------------------------------------------------------------
// TODO: Better class description
// rotate M and S channels towards or away the L-axis

/*
///// args /////
in: stereo input signal
angle: balance angle in radians, from perspective along x-axis. Limits [-pi/4, pi/4]
TODO: Check angle limits

///// returns ////
stereo balanced signal
*/

BlumBalance : BlumUGen {
	*ar { |in, angle|
		var left, right;
		var balL, balR;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		balL = 2.sqrt * ((pi/4) - angle).cos * left;
		balR = 2.sqrt * ((pi/4) - angle).sin * right;

		^[balL, balR]
	}

	*arMS {|in, angle|
		var inM, inS;
		var balM, balS;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		inM = in[0];
		inS = in[1];

		balM = (angle.cos * inM) + (angle.sin * inS);
		balS = (angle.sin * inM) + (angle.cos * inS);

		^[balM, balS];
	}

	*kr { |in, angle|
		var left, right;
		var balL, balR;

		// check input is stereo
		this.confirmStereoInputs(in);

		#left, right = in;

		balL = 2.sqrt * ((pi/4) - angle).cos * left;
		balR = 2.sqrt * ((pi/4) - angle).sin * right;

		^[balL, balR]
	}
}

//-----------------------------------------------------------------------
// rotate only the M-axis, keeping the S-axis in place
/*
///// args /////
in: MS-domain signal in the format [M, S]
angle: mPan angle in radians, from perspective along x-axis. Limits [-pi/2, pi/2]
TODO: Check angle limits

///// returns ////
M-panned MS signal in the format [M, S]
*/

BlumMPan : BlumUGen {
	*arMS { |in, angle|
		var mPanM, mPanS;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		mPanM = in[0] * angle.cos;
		mPanS = in[0] * angle.sin + in[1];

		^[mPanM, mPanS]
	}

	*ar { |in, angle|
		var msSig;

		// check input is stereo
		this.confirmStereoInputs(in);

		// convert to MS domain
		msSig = BlumLRtoMS.ar(in);

		// apply transformation
		msSig = BlumMPan.arMS(msSig, angle);

		// convert back to LR domain
		^BlumMStoLR.ar(msSig);
	}

	*kr { |in, angle|
		var msSig;

		// check input is stereo
		this.confirmStereoInputs(in);

		// convert to MS domain
		msSig = BlumLRtoMS.kr(in);

		// apply transformation
		msSig = BlumMPan.arMS(msSig, angle);

		// convert back to LR domain
		^BlumMStoLR.kr(msSig);
	}
}

//-----------------------------------------------------------------------
// rotate only the S-axis, keeping the M-axis in place
/*
///// args /////
in: MS-domain signal in the format [M, S]
angle: asymmetry angle in radians, from perspective along x-axis. Limits [-pi/2, pi/2]
TODO: Check angle limits

///// returns ////
Asymmetry-transformed MS signal in the format [M, S]

TODO: include BlumMPanLR? name just BlumMPan?
*/

BlumAsym : BlumUGen {
	*arMS { |in, angle|
		var asymM, asymS;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		asymM = in[0] - (in[1] * angle.sin);
		asymS = in[1] * angle.cos;

		^[asymM, asymS]
	}

	*ar { |in, angle|
		var msSig;
		var asymM, asymS;

		// check input is stereo
		this.confirmStereoInputs(in);

		// convert to MS domain
		msSig = BlumLRtoMS.ar(in);

		// apply transformation
		msSig = BlumAsym.arMS(msSig, angle);

		// convert back to LR domain
		^BlumMStoLR.ar(msSig);
	}

	*kr { |in, angle|
		var msSig;
		var asymM, asymS;

		// check input is stereo
		this.confirmStereoInputs(in);

		// convert to MS domain
		msSig = BlumLRtoMS.kr(in);

		// apply transformation
		msSig = BlumAsym.arMS(msSig, angle);

		// convert back to LR domain
		^BlumMStoLR.kr(msSig);
	}
}

//-----------------------------------------------------------------------
// rotate only the R-axis, keeping the L-axis in place

/*
///// args /////
in: stereo signal
angle: R-pan angle in radians, from perspective along x-axis.
TODO: Check angle limits

///// returns ////
R-panned stereo signal
*/
BlumRPan : BlumUGen {
	*ar { |in, angle|
		var rPanL, rPanR;

		// check input is stereo
		this.confirmStereoInputs(in);

		rPanL = in[0] + (in[1] * angle.sin);
		rPanR = in[1] * angle.cos;

		^[rPanL, rPanR]
	}

	// MS->MS transformation
	*arMS { |in, angle|
		var lrSig;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		// convert to LR domain
		lrSig = BlumMStoLR.ar(in);

		// apply transformation
		lrSig = BlumRPan.ar(lrSig, angle);

		// transform back to MS domain
		^BlumLRtoMS.ar(lrSig)
	}

	*kr { |in, angle|
		var rPanL, rPanR;

		// check input is stereo
		this.confirmStereoInputs(in);

		rPanL = in[0] + (in[1] * angle.sin);
		rPanR = in[1] * angle.cos;

		^[rPanL, rPanR]
	}
}

//-----------------------------------------------------------------------
// rotate only the L-axis, keeping the R-axis in place

/*
///// args /////
in: stereo signal
angle: L-pan angle in radians, from perspective along x-axis.
TODO: Check angle limits

///// returns ////
L-panned stereo signal
*/

BlumLPan : BlumUGen {
	*ar { |in, angle|
		var lPanL, lPanR;

		// check input is stereo
		this.confirmStereoInputs(in);

		lPanL = in[0] * angle.cos;
		lPanR = (in[0] * angle.sin).neg + in[1];
		^[lPanL, lPanR]
	}

	// MS->MS transformation
	*arMS { |in, angle|
		var lrSig;

		// check input has two channels (i.e. [M, S])
		this.confirmStereoInputs(in);

		// convert to LR domain
		lrSig = BlumMStoLR.ar(in);

		// apply transformation
		lrSig = BlumLPan.ar(lrSig, angle);

		// transform back to MS domain
		^BlumLRtoMS.ar(lrSig)
	}

	*kr { |in, angle|
		var lPanL, lPanR;

		// check input is stereo
		this.confirmStereoInputs(in);

		lPanL = in[0] * angle.cos;
		lPanR = (in[0] * angle.sin).neg + in[1];
		^[lPanL, lPanR]
	}
}
