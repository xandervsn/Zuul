/* Plays Zuul, a game that moves the player along a map with items, all in console
 * Alexander Siruno-Nebel, Java Programming Period 6
 * December 6th, 2021
 */


package Zuul;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 */

class Game  //sets up game
{
	boolean hasItem = false;
	String ifItem = "";
	
    private Parser parser;
    private Room currentRoom;
    boolean winCondition = false;
    Room spawn, river, clearing, mountain, hut, cave, clearing2, stream, forest, helicopter, treehouse, pit, tree, helipad, lake;
        
    ArrayList<Item> inventory = new ArrayList<Item>();
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }
    
    public static void main(String[] args) {
    	Game zuul = new Game();
    	zuul.play();  
    }
    
    
    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {
      
        // create the rooms
        spawn = new Room("in a small grassland along a beach");
        river = new Room("along the banks of a river. The water is too dangerous to swim across.");
        clearing = new Room("in a large clearing along the river.");
        mountain = new Room("on a steep mountain. Watch your step, it looks dangerous.");
        hut = new Room("on a small wooded platform... CRASH! The ground beneath you gives way. You are back in the clearing.");
        cave = new Room("in a damp cave. You feel something watching you...");
        clearing2 = new Room("in a large clearing away from the river.");
        stream = new Room("along a small stream. The water is too dangerous to swim across.");
        forest = new Room("in a small forest along the clearing. You can see discarded trash around you.");
        helicopter = new Room("next to a crashed helicopter. It's unuseable - not that you would know how to use it. There's some spare gas still in the engine.");
        treehouse = new Room("in a makeshift platform in the canopy.");
        pit = new Room("in a damp, dark pit. You're soaked.");
        tree = new Room("beside a huge tree.");
        helipad = new Room("next to a working helicopter. You might be able to use this to escape... It's missing keys, and gas though. Besides, you wouldn't know how to pilot it.");
        lake = new Room("along the bank of a lake. It's too dangerous to swim across.");
        
        
        // initialise room exits
        spawn.setExit("north", lake);
        spawn.setExit("east", river);
        river.setExit("east", clearing);
        river.setExit("north", lake);
        river.setExit("west", spawn);
        lake.setExit("south", spawn);
        lake.setExit("north", helipad);
        clearing.setExit("west", river);
        clearing.setExit("south", mountain);
        clearing.setExit("east", clearing2);
        clearing.setExit("north", helicopter);
        mountain.setExit("north", clearing);
        mountain.setExit("west", hut);
        mountain.setExit("east", cave);
        clearing2.setExit("west", clearing);
        clearing2.setExit("east", stream);
        clearing2.setExit("north", forest);
        stream.setExit("west", clearing2);
        forest.setExit("south", clearing2);
        helicopter.setExit("south", clearing);
        helicopter.setExit("north", treehouse);
        treehouse.setExit("west", tree);
        treehouse.setExit("south", helicopter);
        // treehouse.setExit("", pit);
        // hut.setExit("", river);
        tree.setExit("south", helipad);
        helipad.setExit("north", tree);
        pit.setExit("west", helipad);
        cave.setExit("west", mountain);
        
        //set items in rooms
        lake.setItem(new Item("oars"));
        river.setItem(new Item("boat"));
        helicopter.setItem(new Item("gas"));
        stream.setItem(new Item("keys"));
        forest.setItem(new Item("pilots_manual"));
        cave.setItem(new Item("machete"));

        currentRoom = spawn;  // start game outside
        
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Adventure!");
        System.out.println("Adventure is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    private boolean processCommand(Command command) 
    {
    	//keeps game going
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help"))
            printHelp();
        else if (commandWord.equals("go")) {
            wantToQuit = goRoom(command);
        }else if (commandWord.equals("quit")) {
            wantToQuit = quit(command); // win condition
        }else if (commandWord.equals("inventory")) {
        	printInventory();
        }else if(commandWord.equals("get")) {
        	getItem(command);
        }else if(commandWord.equals("drop")) {
        	dropItem(command);
        }
        return wantToQuit;
    }

    private void dropItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to drop item.
        Item newItem = null;
        int index = 0;
        for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getDescription().equals(item)) {
				newItem = inventory.get(i);
				index = i;
			}
		}

        if (newItem == null)
            System.out.println("That item is not in your inventory!");
        else { //drops item, sets item in room
            inventory.remove(index);
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped: " + item);
            
        }
    }

    private void getItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to pick up...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to get item room.
        Item newItem = currentRoom.getItem(item);

        if (newItem == null) {
            System.out.println("That item is not here!");
        }else { //picks up item, removes it from room
            inventory.add(newItem);
            currentRoom.removeItem(item);
            System.out.println("You picked up: ");
            System.out.println(item);
        }
    }

	private void printInventory() { //prints your inventory in console
		String output = "";
		for (int i = 0; i < inventory.size(); i++) {
			output += inventory.get(i).getDescription() + " ";
		}
		System.out.println("You are carrying:");
		System.out.println(output);
	}

	/**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() //prints help 
    {
        System.out.println("You find yourself in a clearing in the woods, alone. You should probably find a way out.");
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private boolean goRoom(Command command)  // tries to go into specified room
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return false;
        }

        String direction = command.getSecondWord();
        
        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null) {
            System.out.println("There is nothing this direction!!");
        }else if(currentRoom == river && nextRoom == clearing) {// checks if player has necessary item
        	itemCheck(river);
        }else if (currentRoom == lake && nextRoom == helipad) {
        	itemCheck(lake);
        }else if (currentRoom == helicopter && nextRoom == treehouse) {
        	itemCheck(helicopter);
        }else if (currentRoom == treehouse && nextRoom == tree) {
        	itemCheck(treehouse);
        }else if (nextRoom == hut) {
        	System.out.println(hut.getLongDescription());
        	currentRoom = clearing;
        }
        else {//sees if you win
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            if(currentRoom == helipad) {
            	itemCheck(helipad);
            	if (winCondition = true) {
            		return true;//you win!
            	}
            }
        }
        return false;
    }
    
    private boolean itemCheck(Room room) { //checks to see if player has needed items to cross a body of water
        
        // Try to leave current room.
    	String output = "";
		for (int i = 0; i < inventory.size(); i++) {output += inventory.get(i).getDescription() + " ";}
		if(room == river || room == lake) {
			if(output.contains("boat") && output.contains("oars")) {
				if(room == river) {
					currentRoom = clearing;
					System.out.println(currentRoom.getLongDescription());
				}else if (room == lake) {
					currentRoom = helipad;
					System.out.println(currentRoom.getLongDescription());
				}
			}else if(output.contains("boat")) {
				System.out.println("You have a boat, but no way to move across the river. If only you had some oars...");
			}else System.out.println("The water is too dangerous to swim across! If only you had something else...");
		}else if (output.contains("machete") && room == helicopter) {
			currentRoom = treehouse;
			System.out.println(currentRoom.getLongDescription());
		}else if (room == helicopter) {
			System.out.println("The brush here is thick... if only you had something to cut through.");
		}else if (room == treehouse) {
			if(output.contains("boat") && output.contains("oars")) {
				System.out.println("The zipline buckles under the weight of your boat and oars... CRASH!");
				currentRoom = pit;
				System.out.println(currentRoom.getLongDescription());
			}else {
				System.out.println("You glide across the zipline.");
				System.out.println(currentRoom.getLongDescription());
				currentRoom = tree;
			}
		}else if(room == helipad && output.contains("gas") && output.contains("pilots_manual") && output.contains("keys")) {
			System.out.println("You figure out how to pilot the helicopter, and fly out of the forest. Congratulations! You win!");
			return true;
		}return false;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game. Return true, if this command
     * quits the game, false otherwise.
     */
    private boolean quit(Command command) //quits game
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else
            return true;  // signal that we want to quit
    }
}
