#Fb-Events

##User stories:

 - As a user, I will be able to view Event details and conversations/posts.
 - As a user, I will log into the application with my Facebook Profile
 - As a user, I will be able to post posts on events.
 - As a user, I can confirm or deny attending events

##Pages:
	###Loading page -> fb-events logo.
 			- Loads the facebook-login page, or the event-view
 			page depending on whether the user is logged in or not.
	###facebook-login -> Allows the user to log into facebook.
			- Directs to the event-view page after successful login
	###event-view -> Events listed in a ListView
			- For now, we are displaying 8 events.
			-Events are clickable, opening -> event-description
			- Event blocks display, name, icon, accept/maybe/decline buttons, (possibly date)
	###event-description -> 
			- Displays event's description
			- Displays event's cover photo
			- displays event's first post
			- TextBox input to post to event wall
			- (Optional) view attending button -> opens (Optional) View-Attending-Page
	###(Optional) View Attending-Page
			-Listview of all people attending the event.
---

##Optional Stories (We have a shitload):
			- As a user, I can sync Events to my google calendar
			- As a user, I will be able to view a map of the event locations I am attending
			- As a user, I can get directions to an event location
			- As a user, I can accept, decline, or maybe the event invitation on the notifcation
			- As a user, I will have the ability to sort events by date/time and alphabetically
			- As a user, I will be notified about upcoming events within the current day.
	##Information about Optional Stories:
		###Google Calendar: 
		[Android Calendar Providor](http://developer.android.com/guide/topics/providers/calendar-provider.html)
