#!/bin/bash
set -x

PASSED=0

DISABLE_TESTS=TRUE scala220 TestDisabledTests.scala && PASSED=`expr $PASSED + 1`
DISABLE_TESTS=FALSE scala220 TestDisabledTests.scala || PASSED=`expr $PASSED + 1`
DISABLE_TESTS=TRUE scala220 TestEnabledTests.scala || PASSED=`expr $PASSED + 1`
DISABLE_TESTS=FALSE scala220 TestEnabledTests.scala && PASSED=`expr $PASSED + 1`

echo "Passed $PASSED / 4 tests."

echo "Interactive tests:"
echo "You should not see a new window or any GTK warnings"
DISABLE_JAVAFX=TRUE scala220 JavaFX.scala
echo "Now trying to enable JavaFX"
DISABLE_JAVAFX=FALSE scala220 JavaFX.scala
