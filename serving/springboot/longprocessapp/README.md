# Long Process App
You can set the execution seconds in application.properties

It's required to set the slack_token variable in env variable since it would send message to your slack channel.

You need to setup the slack app and it's permission in api.slack.com before you run this app

## in Fish shell
set -x slack_token your_slack_bot_token

## in Bash Shell
export slack_token=your_slack_bot_token
