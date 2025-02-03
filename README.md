# event-manager-project
This project is about developing a backend API for an event platform where event organizers can publish events, users can register for events, and organizers can accept or reject registration requests. The API will be built using Java Spring Boot.

Core Features added to the project : 
1. User Management ( with Authentication )
   
  ○ Users can sign up and log in.
  
  ○ Each user can be an Organizer or an Attendee.
  
  ○ Implement role-based access to restrict actions (e.g., only organizers can publish events…).
  
  ○ A super admin role should be added to see all Organizers and Attendees related information.

3. Event Management

  ○ Organizers can:
  
    ■ Create, update, and delete events.
    
    ■ View a list of users who have registered for their events.
    
  ○ Events should include:
  
    ■ Title, description, location, start date, end date, and maximum capacity.
    
    ■ An optional image/gallery for the event.
  ○ Super admins can :
    
    ■ View a list of events with related data (only registration requests count).
4. Registration Management

  ○ Attendees can register for published events and see the registration status.
  
  ○ Organizers can accept or reject registration requests.

6. Search and Filtering
 
  ○ Attendees can search for events by title, location, or date.
  
  ○ Filter events by upcoming or past events.
  
  ○ Super admin can perform the same search operations.

To use this Project u need to download Elipse ,Mysql, Lombok,Java.
to Run the project just go to Com.springprjt.springboot and go to EventplatfhgdsApplication.java and then run the app as spring boot 
now u just load this url : http://localhost:8080/swagger-ui/index.html#/
in this url u will find the entire endpoints and u can see how they work
