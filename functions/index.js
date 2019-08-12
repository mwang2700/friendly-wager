const { Logging } = require('@google-cloud/logging');
const functions = require('firebase-functions');
const axios = require('axios');
const admin = require('firebase-admin');
var key = require('./api.js');

admin.initializeApp();

var basketball = false;
var baseball = false;
var football = false;

const logging = new Logging();
const log = logging.log('DEBUG_VALUES');

const METADATA = {
  resource: {
    type: 'cloud_function',
    labels: {
      function_name: 'DebugLogging',
      region: 'us-central1'
    }
  }
};
const logData = {
  event: 'db_write',
  value: 'baseball',
};


const getSports = async () => {
	try {
		return await axios.get('https://api.the-odds-api.com/v3/sports', {
			params: {
				api_key: key.apiKey
			}
		})
	} catch (error) {
		console.error(error)
		return null;
	}
}

const updateSports = async () => {
	const sports = await getSports()
	
	var i;
	if (sports.data.success) {
		for (i = 0; i < sports.data.data.length; i++) {
			if (sports.data.data[i].key === "baseball_mlb") {
				baseball = true;
			} else if (sports.data.data[i].key === "americanfootball_nfl") {
				football = true;
			} else if (sports.data.data[i].key === "basketball_nba") {
				basketball = true;
			}
		}
		return true;
	} else {
		return false;
	}
}


const getBaseball = async () => {
	try {
		return await axios.get('https://api.the-odds-api.com/v3/odds', {
			params: {
				api_key: key.apiKey,
				sport: 'baseball_mlb',
				region: 'us', // uk | us | au
				mkt: 'h2h' // h2h | spreads | totals
			}
		})
	} catch (error) {
		console.error(error)
		return null;
	}
}

const updateBaseball = async () => {
	const baseball = await getBaseball()
	if (baseball.data.success) {
		const ref = admin.database().ref();
		var numData = baseball.data.data.length;
		var b;
		var baseballRef = ref.child("odds").child("mlb")
		for (b = 0; b < numData; b++) {
			if (baseball.data.data[b].sites_count > 0) {
				var teamone = baseball.data.data[b].teams[0];
				teamone = teamone.replace(/\./g,' ');
				var teamtwo = baseball.data.data[b].teams[1];
				teamtwo = teamtwo.replace(/\./g,' ');
				var t1odds = baseball.data.data[b].sites[0].odds.h2h[0];
				var t2odds = baseball.data.data[b].sites[0].odds.h2h[1];
				baseballRef.child(teamone.concat(teamtwo))
					.child(teamone)
					.child("multiplier").set(t1odds);
				baseballRef.child(teamone.concat(teamtwo))
					.child(teamtwo)
					.child("multiplier").set(t2odds);
				baseballRef.child(teamone.concat(teamtwo))
					.child("time").set(baseball.data.data[b].commence_time);
			}
		}
		return true;
	} else {
		return false;
	}
}

const getFootball = async () => {
	try {
		return await axios.get('https://api.the-odds-api.com/v3/odds', {
			params: {
				api_key: key.apiKey,
				sport: 'americanfootball_nfl',
				region: 'us', // uk | us | au
				mkt: 'h2h' // h2h | spreads | totals
			}
		})
	} catch (error) {
		console.error(error)
		return null;
	}
}

const updateFootball = async () => {
	const football = await getFootball()
	
	if (football.data.success) {
		const ref = admin.database().ref();
		var numData = football.data.data.length;
		var c;
		var footballRef = ref.child("odds").child("nfl")
		for (c = 0; c < numData; c++) {
			if (football.data.data[c].sites_count > 0) {
				var teamone = football.data.data[c].teams[0];
				teamone = teamone.replace(/\./g,' ');
				var teamtwo = football.data.data[c].teams[1];
				teamtwo = teamtwo.replace(/\./g,' ');
				var t1odds = football.data.data[c].sites[0].odds.h2h[0];
				var t2odds = football.data.data[c].sites[0].odds.h2h[1];
				footballRef.child(teamone.concat(teamtwo))
					.child(teamone)
					.child("multiplier").set(t1odds);
				footballRef.child(teamone.concat(teamtwo))
					.child(teamtwo)
					.child("multiplier").set(t2odds);
				footballRef.child(teamone.concat(teamtwo))
					.child("time").set(football.data.data[c].commence_time);
			}
		}
		return true;
	} else {
		return false;
	}
}

const getBasketball = async () => {
	try {
		return await axios.get('https://api.the-odds-api.com/v3/odds', {
			params: {
				api_key: key.apiKey,
				sport: 'basketball_nba',
				region: 'us', // uk | us | au
				mkt: 'h2h' // h2h | spreads | totals
			}
		})
	} catch (error) {
		console.error(error)
		return null;
	}
}

const updateBasketball = async () => {
	const basketball = await getBasketball()
	
	if (basketball.data.success) {
		const ref = admin.database().ref();
		var numData = basketball.data.data.length;
		var d;
		var basketballRef = ref.child("odds").child("nba")
		for (d = 0; d < numData; d++) {
			if (basketball.data.data[d].sites_count > 0) {
				var teamone = basketball.data.data[d].teams[0];
				teamone = teamone.replace(/\./g,' ');
				var teamtwo = basketball.data.data[d].teams[1];
				teamtwo = teamtwo.replace(/\./g,' ');
				var t1odds = basketball.data.data[d].sites[0].odds.h2h[0];
				var t2odds = basketball.data.data[d].sites[0].odds.h2h[1];
				basketballRef.child(teamone.concat(teamtwo))
					.child(teamone)
					.child("multiplier").set(t1odds);
				basketballRef.child(teamone.concat(teamtwo))
					.child(teamtwo)
					.child("multiplier").set(t2odds);
				basketballRef.child(teamone.concat(teamtwo))
					.child("time").set(basketball.data.data[d].commence_time);
			}
		}
		return true;
	} else {
		return false;
	}
}

const checkMatches = async () => {
	const oddsRef = admin.database().ref().child("odds");
	oddsRef.once("value")
		.then(function(snapshot) {
			snapshot.forEach(function(childSnapshot) {
				childSnapshot.forEach(function(gameSnapshot) {
					var time = gameSnapshot.child("time").val();
					if ((Date.now() / 1000) >= time) {
						gameSnapshot.ref.remove();
					} 
				});
			});
			return true;
		})
		.catch(error => {
			console.log(error)
		});
	
}

const getMLBData = async () => {
	try {
		return await axios.get('http://site.api.espn.com/apis/site/v2/sports/baseball/mlb/scoreboard')
	} catch (error) {
		const entry = log.entry(METADATA, logData);
		log.write(entry)
		return null;
	}
}

const updateScore = async (pointsRef, pointsEarned)  => {
	return await pointsRef.transaction(currentPoints => {
		return Math.trunc(currentPoints + pointsEarned);
	});
}

const cycleMatches = async (combined, winner, groupName, userRef, username) => {
	matchesPromises = [];
	await userRef.forEach(betSnapshot => {
		var currentTeams = betSnapshot.key;
		if (currentTeams === combined) {
			if (betSnapshot.hasChild(winner)) {
				var thePointsRef = admin.database().ref().child(groupName).child(username).child("points");
				var thePointsEarned = betSnapshot.child(winner).child("amount").val() * betSnapshot.child(winner).child("multiplier").val();
				matchesPromises.push(updateScore(thePointsRef, thePointsEarned))
			}
			betSnapshot.ref.remove();
		}
	});
	return Promise.all(matchesPromises);
}
		


const cycleUsers = async (combined, winner, groupName, groupRef) => {
	usersPromises = [];
	await groupRef.forEach(userSnapshot => {
		var theUsername = userSnapshot.key;
		var theUserRef = userSnapshot;
		usersPromises.push(cycleMatches(combined, winner, groupName, theUserRef, theUsername))
	})
	
	return Promise.all(usersPromises);
	
}

const cycleGroups = async (sport, combined, winner)  => {
	const betsRef = admin.database().ref().child("bets").child(sport);
	var groupsPromises = [];
	
	await betsRef.once("value")
		.then(async snapshot => {
			await snapshot.forEach(groupsSnapshot => {
				var theGroupName = groupsSnapshot.key;
				var theGroupRef = groupsSnapshot;
				
				groupsPromises.push(cycleUsers(combined, winner, theGroupName, theGroupRef));
			});
			return true;
		})
	return Promise.all(groupsPromises);
}

const getMLB = async () => {
	const data = await getMLBData();
	
	var numGames = data.data.events.length;
	var a;
	var thewinner;
	var combinedteams;
	var promises = [];
	for (a = 0; a < numGames; a++) {
		if (data.data.events[a].status.type.completed) {
			var team1 = data.data.events[a].competitions[0].competitors[0].team.displayName;
			team1 = team1.replace(/\./g,' ');
			var team2 = data.data.events[a].competitions[0].competitors[1].team.displayName;
			team2 = team2.replace(/\./g,' ');
			combinedteams = team1.concat(team2);	
			if (data.data.events[a].competitions[0].competitors[0].winner) {
				thewinner = team1;
			} else {
				thewinner = team2;
			}
			promises.push(cycleGroups("mlb", combinedteams, thewinner));
		}
	}
	return Promise.all(promises);
}

const getNFLData = async () => {
	try {
		return await axios.get('http://site.api.espn.com/apis/site/v2/sports/football/nfl/scoreboard')
	} catch (error) {
		const entry = log.entry(METADATA, logData);
		log.write(entry); 
		return null;
	}
}

const getNFL = async () => {
	const data = await getNFLData();
	
	var numGames = data.data.events.length;
	var b;
	var theWinner;
	var combinedTeams;
	var NFLpromises = [];
	for (b = 0; b < numGames; b++) {
		if (data.data.events[b].status.type.completed) {
			var NFLteam1 = data.data.events[b].competitions[0].competitors[0].team.displayName;
			NFLteam1 = NFLteam1.replace(/\./g,' ');
			var NFLteam2 = data.data.events[b].competitions[0].competitors[1].team.displayName;
			NFLteam2 = NFLteam2.replace(/\./g,' ');
			combinedTeams = NFLteam1.concat(NFLteam2);	
			if (data.data.events[b].competitions[0].competitors[0].winner) {
				theWinner = team1;
			} else {
				theWinner = team2;
			}
			NFLpromises.push(cycleGroups("nfl", combinedTeams, theWinner));
		}
	}
	return Promise.all(NFLpromises);
} 

/* const getNBAData = async () => {
	try {
		return await axios.get('http://site.api.espn.com/apis/site/v2/sports/basketball/nba/scoreboard')
	} catch (error) {
		const entry = log.entry(METADATA, logData);
		log.write(entry); 
		return null;
	}
}

const getNFL = async () => {
	const data = await getNFLData();
	
	var numGames = data.data.events.length;
	var c;
	var theWinner;
	var combinedTeams;
	var NBApromises = [];
	for (c = 0; c < numGames; c++) {
		if (data.data.events[c].status.type.completed) {
			var NBAteam1 = data.data.events[c].competitions[0].competitors[0].team.displayName;
			NBAteam1 = NBAteam1.replace(/\./g,' ');
			var NBAteam2 = data.data.events[c].competitions[0].competitors[1].team.displayName;
			NBAteam2 = NBAteam2.replace(/\./g,' ');
			combinedTeams = NBAteam1.concat(NBAteam2);	
			if (data.data.events[c].competitions[0].competitors[0].winner) {
				theWinner = team1;
			} else {
				theWinner = team2;
			}
			NBApromises.push(cycleGroups("nba", combinedTeams, theWinner));
		}
	}
	return Promise.all(NBApromises);
}  */

exports.scheduledFunctionCrontab = functions.pubsub.schedule('0 8 * * *')
  .timeZone('America/New_York')
  .onRun((context) => {
	  
	return updateSports()
	.then(updateBaseball())
	.then(updateFootball())
	.then(updateBasketball())
	.catch(error => {
		console.log(error)
	});
});

exports.scheduleFunctionCrontabTwo =  functions.pubsub.schedule('* * * * *')
  .timeZone('America/New_York')
  .onRun((context) => {
	  
	return checkMatches()
	.catch(error => {
		console.log(error)
	});
	  
});

exports.scheduleFunctionCrontabThree = functions.pubsub.schedule('*/15 * * * *')
  .timeZone('America/New_York')
  .onRun((context) => {
		
	return getMLB()
	.then(getNFL())
	.catch(error => {
		console.log(error)
	});
	
	  
	  
});