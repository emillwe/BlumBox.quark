/*
BlumRotate: wrapper for Rotate2, using stereo input and degree arguments

///// args /////
in: stereo input signal
angle: rotation angle in degrees, from perspective along x-axis. Limits [-180, 180]

///// returns ////
stereo rotated signal
*/

BlumRotate {
	*ar { |in, angle|
		// TODO: wrap angle inside bounds
		// TODO: check for stereo input

		// normalize degrees for Rotate2 pos argument
		var pos = angle / -180.0;

		^Rotate2.ar(in[0], in[1], pos);
	}

	*kr { |in, angle|
		// TODO: is control rate rotation necessary?
		// TODO: wrap angle inside bounds
		// TODO: check for stereo input
		var pos = angle / -180.0;

		^Rotate2.kr(in[0], in[1], pos);
	}
}