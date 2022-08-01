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
		reg = -192;  // regularization factor
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
				normfac = 1;

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
		var leftA, rightA;
		var left2, right2, multLR, correlation;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		// normalization factor
		normfac = 2;

		case(
			{ method == \instant }, {
				/*// normfac = 2 * 2.sqrt;
				normfac = 2;

				// magnitude^2 of analytic signal
				left2 = HilbertW.ar(left, size).squared.sum;
				right2 = HilbertW.ar(right, size).squared.sum;
				/*left2 = HilbertW.ar(left, size).squared.sum.squared;
				right2 = HilbertW.ar(right, size).squared.sum.squared;*/

				// analytic left and right
				/*left2 = HilbertW.ar(left, size);
				right2 = HilbertW.ar(right, size);*/

				// magnitude squared
				/*left2 = (left2.squared).sum;
				right2 = (right2.squared).sum;*/
				/*left2 = left2.squared;
				right2 = right2.squared;*/

				// TODO: is this right?
				// multLR = HilbertW.ar(left * right, size).sum;
				// multLR = HilbertW.ar(left * right, size).squared.sum;
				// multLR = HilbertW.ar(left * right, size).squared.sum.squared;

				// analytic sum multiply
				multLR = (left2 * right2).sum;*/

				// analytic left and right
				leftA = HilbertW.ar(left, size);
				rightA = HilbertW.ar(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				// analytic sum multiply
				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// running sum of square
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

		^correlation
	}

	*kr { |in, size = 2048, method = \instant|
		var left, right;
		var leftA, rightA;
		var left2, right2, multLR, correlation;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		// normalization factor
		normfac = 2;

		case(
			{ method == \instant }, {
				// analytic left and right
				leftA = HilbertW.kr(left, size);
				rightA = HilbertW.kr(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				// analytic sum multiply
				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// running sum of square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size);

				// running sum multiply
				multLR = RunningSum.kr(left * right, size);
			}
		);

		// the regularization - avoid divide by zero!
		reg = DC.kr(this.reg.dbamp);

		// analyze balance
		correlation = normfac * multLR / ((left2 + right2) + reg);

		^correlation
	}
}

// calculate encoding angle (in radians) of a stereophonic signal
BlumFollowAngle : BlumEval {
	*ar { |in, size = 2048, method = \instant|
		var left, right;
		var leftA, rightA;
		var left2, right2, multLR, angle;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// normalization factor
				// normfac = 2 / (2.sqrt - 1);
				// normfac = (2.sqrt - 1) / 2;
				normfac = 0.5;

				// analytic left and right
				leftA = HilbertW.ar(left, size);
				rightA = HilbertW.ar(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// normalization factor
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
		var leftA, rightA;
		var left2, right2, multLR, angle;
		var reg;
		var normfac;

		// check input is stereo
		this.confirmStereoInputs(in);

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// normalization factor
				// normfac = 2 / (2.sqrt - 1);
				// normfac = (2.sqrt - 1) / 2;
				normfac = 0.5;

				// analytic left and right
				leftA = HilbertW.kr(left, size);
				rightA = HilbertW.kr(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// normalization factor
				normfac = 0.5;

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
		angle = normfac * atan2(left2 - right2, (2 * multLR) + reg);

		^angle
	}
}

BlumFollowRadius : BlumEval {
	*ar { |in, size = 2048, method = \radius|
		var left, right;
		var leftA, rightA;
		var left2, right2, multLR;
		var left2squared, right2squared, twoLeft2right2;
		var radius;
		var reg, regGain = -180.0;

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// analytic left and right
				leftA = HilbertW.ar(left, size);
				rightA = HilbertW.ar(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				// analytic sum multiply
				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// running sum of square
				left2 = RunningSum.ar(left.squared, size);
				right2 = RunningSum.ar(right.squared, size);

				// running sum multiply
				multLR = RunningSum.ar(left * right, size);
			}
		);

		// magnitude squared, squared
		left2squared = left2.squared;
		right2squared = right2.squared;

		// another intermediate value
		twoLeft2right2 = 2 * left2 * right2;

		// the regularization - avoid divide by zero!
		reg = DC.ar(regGain.dbamp);

		// analyze radius
		radius = (
			left2.squared + (4 * multLR.squared) - twoLeft2right2 + right2.squared
		) / (
			left2.squared + twoLeft2right2 + right2.squared + reg
		);

		// return
		^radius;
	}

	*kr { |in, size = 2048, method = \radius|
		var left, right;
		var leftA, rightA;
		var left2, right2, multLR;
		var left2squared, right2squared, twoLeft2right2;
		var radius;
		var reg, regGain = -180.0;

		// extract left and right
		#left, right = in;

		case(
			{ method == \instant }, {
				// analytic left and right
				leftA = HilbertW.kr(left, size);
				rightA = HilbertW.kr(right, size);

				// magnitude squared
				left2 = leftA.squared.sum;
				right2 = rightA.squared.sum;

				// analytic sum multiply
				multLR = (leftA * rightA).sum;
			},
			{ method == \average }, {
				// running sum of square
				left2 = RunningSum.kr(left.squared, size);
				right2 = RunningSum.kr(right.squared, size);

				// running sum multiply
				multLR = RunningSum.kr(left * right, size);
			}
		);

		// magnitude squared, squared
		left2squared = left2.squared;
		right2squared = right2.squared;

		// another intermediate value
		twoLeft2right2 = 2 * left2 * right2;

		// the regularization - avoid divide by zero!
		reg = DC.kr(regGain.dbamp);

		// analyze radius
		radius = (
			left2.squared + (4 * multLR.squared) - twoLeft2right2 + right2.squared
		) / (
			left2.squared + twoLeft2right2 + right2.squared + reg
		);

		// return
		^radius;
	}
}
