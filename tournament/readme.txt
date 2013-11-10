Configurations:

- Distributions
1. power2.txt
2. random.txt
3. uniform.txt
4. uniform2.txt
5. uniform6.txt

- bowl size
1, 12, 36, 100
Did not run 1200, not enough time

- players
9players.txt - one for each group, 12 players in total (some groups have multiple subimssions)
10players.txt - one for each group, including one dumb player, 13 players in total
20players.txt - two for each group, and two dumb players, 26 players in total

- iterations
Each configuration is run 2000 iterations. The number of iterations is chosen to balance the number of valid games. Several groups are very CPU-intensive. I have to balance the iterations and time out limit so that we will not have those groups time out almost in every game.

- timeout
Each move allows at most 50 milliseconds. If a player times out, it will be marked as timeout in the result. And the simulator will never call the pass method of that player in the following steps, i.e. the player will behave as it reject every bowl passed to it and take the last one.


Table schema:

CREATE TABLE game (
       id int NOT NULL AUTO_INCREMENT, // game id
       players int NOT NULL, // number of players
       bowlsz int NOT NULL, // the bowl size
       dist varchar(20) NOT NULL, // distribution name
       self tinyint(1) NOT NULL, // UNUSED IGNORE 
       PRIMARY KEY(id))

CREATE TABLE result (
       game_id int NOT NULL, // game id
       gname varchar(10) NOT NULL,  // group name
       score int NOT NULL, // score of two rounds
       position int NOT NULL, // position of the player
       timeout tinyint(1) NOT NULL) // time out

Import into MySQL

mysql -p -username database_name < file.sql
