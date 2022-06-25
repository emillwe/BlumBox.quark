/*
BlumLRtoMS: convert MS to LR domain

///// args /////
in: MS input signal

///// returns ////
LR signal
*/

BlumLRtoMS {
	*ar { |in|
		var left, right;
		var sum, diff;
		// TODO: check for stereo input

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		left = 2.sqrt.reciprocal * sum;
		right = 2.sqrt.reciprocal * diff;

		^[left, right];
	}

	*kr { |in|
		// TODO: is control rate rotation necessary?
		// TODO: consolidate ar/kr class methods?
		var left, right;
		var sum, diff;
		// TODO: check for stereo input

		sum = in[0] + in[1];
		diff = in[0] - in[1];

		left = 2.sqrt.reciprocal * sum;
		right = 2.sqrt.reciprocal * diff;

		^[left, right];

	}
}