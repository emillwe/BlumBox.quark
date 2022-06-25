/*
BlumLRtoMS: convert LR to MS domain

///// args /////
in: LR input signal

///// returns ////
MS signal
*/

BlumLRtoMS {
	*ar { |in|
		var mid, side;
		var sum, diff;
		// TODO: check for stereo input

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		mid = 2.sqrt.reciprocal * sum;
		side = 2.sqrt.reciprocal * diff;

		^[mid, side];
	}

	*kr { |in|
		// TODO: is control rate rotation necessary?
		// TODO: consolidate ar/kr class methods?
		var mid, side;
		var sum, diff;
		// TODO: check for stereo input

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		mid = 2.sqrt.reciprocal * sum;
		side = 2.sqrt.reciprocal * diff;

		^[mid, side];

	}
}