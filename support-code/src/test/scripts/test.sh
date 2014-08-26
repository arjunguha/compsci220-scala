#!/bin/bash
set -x

PASSED=0

DISABLE_TESTS=TRUE scala220 TestDisabledTests.scala && PASSED=`expr $PASSED + 1`
DISABLE_TESTS=FALSE scala220 TestDisabledTests.scala || PASSED=`expr $PASSED + 1`
DISABLE_TESTS=TRUE scala220 TestEnabledTests.scala || PASSED=`expr $PASSED + 1`
DISABLE_TESTS=FALSE scala220 TestEnabledTests.scala && PASSED=`expr $PASSED + 1`

echo "Passed $PASSED / 4 tests."
