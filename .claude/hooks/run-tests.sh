#!/bin/bash
# Stop hook: run the test suite when Claude finishes a turn.
# Exit 2 blocks the stop and feeds stderr back to Claude, so a turn can't end with failing tests.
# Safety valve: Claude gets one retry. If the tests still fail on the next stop
# (stop_hook_active is true), the turn ends and the user sees a warning instead of looping forever.

stop_hook_active=$(jq -r '.stop_hook_active // false' 2>/dev/null)

cd "${CLAUDE_PROJECT_DIR:-$(dirname "$0")/../..}" || exit 0

output=$(mvn -q test 2>&1)
status=$?

if [ "$status" -ne 0 ]; then
    # Maven's results summary (failed tests with expected/actual) or compiler errors; else the tail.
    summary=$(echo "$output" | awk '
        /^\[ERROR\] (Failures|Errors):|COMPILATION ERROR/ { show = 1 }
        show { print }
        show && /^\[ERROR\] Tests run:/ { exit }
    ' | head -150)
    [ -n "$summary" ] || summary=$(echo "$output" | tail -80)

    if [ "$stop_hook_active" = "true" ]; then
        jq -n --arg summary "$summary" \
            '{systemMessage: ("Tests still failing after one retry; letting the turn end.\n\n" + $summary)}'
        exit 0
    fi

    {
        echo "mvn -q test failed (exit $status). Fix the failures before finishing the turn."
        echo "If a test itself looks wrong, don't change it to make it pass: stop and tell the user (CLAUDE.md)."
        echo "This is your one retry: if the tests still fail at the next stop, the turn will end anyway."
        echo
        echo "$summary"
    } >&2
    exit 2
fi

exit 0
