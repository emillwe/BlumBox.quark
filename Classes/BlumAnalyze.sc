// TODO: Copyright (GPLv3.0?), topic scentence, summary, attribution


//---------------------------------------------------------------------
//	TODO: BlumBox in a sentence
//
// 	Classes: (Superclass) BlumEval, tAvgPower
//
//	TODO: BlumBox Summary
//
//---------------------------------------------------------------------

/*
~-- Attribution --~
TODO: DXARTS 462 DOCUMENTATION FOR ATTRIBUTION

The implementation of all the following stereo imaging transforms are taken
from "Classic Stereo Imaging Transforms--A Review", by Dr. Joseph Anderson
@ DXARTS, University of Washington.
*/

BlumEval : BlumUGen {
	classvar <>reg;

	*initClass {
		reg = -192.dbamp;  // regularization factor
	}
}

// from DXARTS 462
BlumFollowPower : BlumEval {
	*ar { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, power;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				normfac = 0.5;  // is this right?? TEST the two match w/ sinusoid!!
				// normfac = 2.sqrt.reciprocal;

				// magnitude^2 of analytic signal
				left2 = HilbertW.ar(left, size).squared.sum;
				right2 = HilbertW.ar(right, size).squared.sum;
			},
			{ method == \average }, {
				normfac = size.reciprocal;

				// running sum of square
				left2 = RunningSum.ar(left.squared, size);
				right2 = RunningSum.ar(right.squared, size);
			}
		);

		// analyze power
		power = normfac * (left2 + right2);

		^power
	}

	*kr { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, power;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				normfac = 0.5;  // is this right?? TEST the two match w/ sinusoid!!

				// magnitude^2 of analytic signal
				left2 = HilbertW.kr(left, size).squared.sum;
				right2 = HilbertW.kr(right, size).squared.sum;
			},
			{ method == \average }, {
				normfac = size.reciprocal;

				// running sum of square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size);
			}
		);

		// analyze power
		power = normfac * (left2 + right2);

		^power
	}
}

BlumFollowBalance : BlumEval {
	*ar { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, balance;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, { // TODO: Doesn't work!
				// normfac = (2.sqrt); // gives output between +- root 2
				normfac = 2;

				// magnitude^2 of analytic signal
				left2 = HilbertW.ar(left, size).squared.sum;
				right2 = HilbertW.ar(right, size).squared.sum;
			},
			{ method == \average }, {
				normfac = 1;

				// running sum of square
				left2 = RunningSum.ar(left.squared, size);
				right2 = RunningSum.ar(right.squared, size)
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.ar(this.reg.dbamp);

		// analyze balance
		balance = normfac * (left2 - right2) / ((left2 + right2) + reg);

		^balance
	}

	*kr { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, balance;
		var reg;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// magnitude^2 of analytic signal
				left2 = HilbertW.kr(left, size).squared.sum;
				right2 = HilbertW.kr(right, size).squared.sum;
			},
			{ method == \average }, {
				// running sum of square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size)
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.kr(this.reg.dbamp);

		// analyze balance
		balance = (left2 - right2) / ((left2 + right2) + reg);

		^balance
	}
}

BlumFollowCorrelation : BlumEval {
	*ar { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, multLR, correlation;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				normfac = 2 * 2.sqrt;

				// magnitude^2 of analytic signal
				// left2 = HilbertW.ar(left, size).squared.sum;
				// right2 = HilbertW.ar(right, size).squared.sum;
				left2 = HilbertW.ar(left, size).squared.sum.squared;
				right2 = HilbertW.ar(right, size).squared.sum.squared;

				// TODO: is this right?
				// multLR = HilbertW.ar(left * right, size).sum;
				// multLR = HilbertW.ar(left * right, size).squared.sum;
				multLR = HilbertW.ar(left * right, size).squared.sum.squared;
			},
			{ method == \average }, {
				normfac = 2;

				// running sum square
				left2 = RunningSum.ar(left.squared, size);
				right2 = RunningSum.ar(right.squared, size);

				// running sum multiply
				multLR = RunningSum.ar(left * right, size);
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.ar(this.reg.dbamp);

		// analyze balance
		correlation = normfac * multLR / ((left2 + right2) + reg);

		// ^correlation
		// ^[left2, right2, multLR]
		^left2
	}

	*kr { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, multLR, correlation;
		var reg;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// magnitude^2 of analytic signal
				left2 = HilbertW.kr(left, size).squared.sum;
				right2 = HilbertW.kr(right, size).squared.sum;

				// TODO: is this right?
				multLR = HilbertW.kr(left * right, size).sum;
			},
			{ method == \average }, {
				// running sum square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size);

				// running sum multiply
				multLR = RunningSum.kr(left * right, size);
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.ar(this.reg.dbamp);

		// analyze balance
		correlation = 2 * multLR / ((left2 + right2) + reg);

		^correlation
	}
}

// calculate time-average encoding angle (in radians) of a stereophonic signal
BlumFollowAngle : BlumEval {
	*ar { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, multLR, angle;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				normfac = 0.5 * 2.sqrt;

				// magnitude^2 of analytic signal
				left2 = HilbertW.ar(left, size).squared.sum;
				right2 = HilbertW.ar(right, size).squared.sum;

				// TODO: is this right?
				multLR = HilbertW.ar(left * right, size).sum;
			},
			{ method == \average }, {
				normfac = 0.5;

				// running sum square
				left2 = RunningSum.ar(left.squared, size);
				right2 = RunningSum.ar(right.squared, size);

				// running sum multiply
				multLR = RunningSum.ar(left * right, size);
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.ar(this.reg.dbamp);

		// analyze angle
		angle = normfac * atan2(left2 - right2, (2 * multLR) + reg);

		^angle
	}

	*kr { |in, size = 2048, method = \instant|
		var left, right;
		var left2, right2, multLR, angle;
		var reg;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// magnitude^2 of analytic signal
				left2 = HilbertW.kr(left, size).squared.sum;
				right2 = HilbertW.kr(right, size).squared.sum;

				// TODO: is this right?
				multLR = HilbertW.kr(left * right, size).sum;
			},
			{ method == \average }, {
				// running sum square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size);

				// running sum multiply
				multLR = RunningSum.kr(left * right, size);
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.kr(this.reg.dbamp);

		// analyze angle
		angle = 0.5 * atan2(left2 - right2, (2 * multLR) + reg);

		^angle
	}
}
