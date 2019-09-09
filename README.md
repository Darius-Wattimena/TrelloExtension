# TrelloExtension

TrelloExtension is a backend server that uses the trello api to process data and return json that can be used on your desired frontend website.

## Requirements

- Java 8 or higher
- MongoDB 4.0.10 or higher



## Available API calls

### Header meanings

| Header          | Description                                                  |
| --------------- | ------------------------------------------------------------ |
| key             | A trello API key that can be generated at https://trello.com/app-key |
| oauth           | An OAuth token thats required by trello that can be created by a trello user when going to https://trello.com/1/authorize?expiration=1day&name=MyPersonalToken&scope=read&response_type=token&key={YourAPIKey} |
| startDate       | The start date of a sprint (e.g. 2019-09-16)                 |
| endDate         | The end date of a sprint  (e.g. 2019-09-29)                  |
| today           | The day of today (e.g. 2019-09-16)                           |
| doneListId      | A list ID that indicates the "done" list of a Trello board this is provided by Trello |
| doingListId     | A list ID that indicates the "doing" list of a Trello board this is provided by Trello |
| reviewingListId | A list ID that indicates the "reviewing" list of a Trello board this is provided by Trello |
| testingListId   | A list ID that indicates the "testing" list of a Trello board this is provided by Trello |



### Board API calls

One of the most used calls are to receive the board data. In all the call {id} means the board ID that can be found when accessing a board on Trello.

| Call                               |                           Headers                            | Response                                                     |
| ---------------------------------- | :----------------------------------------------------------: | ------------------------------------------------------------ |
| /board/{id}                        |                          key, token                          | Returns a Board object                                       |
| /board/{id}/detailed               |                          key, token                          | Returns a Board object and loads all the lists and cards associated with it |
| /board/{id}/statistics             |                          key, token                          | Returns a Statistics object and counts every label found on each list |
| /board/{id}/lastaction             |                          key, token                          | Returns a Action object thats the last executed action       |
| /board/{id}/burndownchartinfo      | key, token, <br />doneListId,<br />startDate, endDate, today | Returns a BurndownChart object that contains a BurndownChartItem for day. That's needed to generate a BurndownChart |
| /board/{id}/todayburndownchartinfo |           key, token,<br />doneListId,<br />today            | Returns a BurndownChartItem object                           |
| /board/{id}/leaderboard            |                      startDate, endDate                      | Returns a Leaderboard object that contains a LeaderboardItem for every board member |

