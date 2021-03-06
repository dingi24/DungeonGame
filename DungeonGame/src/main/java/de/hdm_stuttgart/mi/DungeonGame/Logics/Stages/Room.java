/*
 * Room.java
 *
 * Class for room creation and managing.
 *
 * author: Sascha W.
 * last edit / by: 2020-01-29 / Sascha W.
 */
package de.hdm_stuttgart.mi.DungeonGame.Logics.Stages;

//Import statements
import de.hdm_stuttgart.mi.DungeonGame.Helper.Logics.Actors.Coin;
import de.hdm_stuttgart.mi.DungeonGame.Helper.Logics.Actors.Inventory;
import de.hdm_stuttgart.mi.DungeonGame.Helper.Logics.Actors.Potion;
import de.hdm_stuttgart.mi.DungeonGame.Helper.Logics.Coordinate;
import de.hdm_stuttgart.mi.DungeonGame.Helper.Logics.Stages.*;
import de.hdm_stuttgart.mi.DungeonGame.Logics.Actors.Enemy;
import de.hdm_stuttgart.mi.DungeonGame.Logics.Enum.Difficulty;
import de.hdm_stuttgart.mi.DungeonGame.Logics.Stages.Enum.Directions;
import de.hdm_stuttgart.mi.DungeonGame.Logics.Stages.Enum.FieldType;
import java.util.ArrayList;

/**
 * Junction of all Field related classes.
 */
public class Room {

    /**
     * random FieldType hight
     */
    private final int HIGHT = (int) (Math.random() * 44) + 6;
    /**
     * random FieldType width
     */
    private final int WIDTH = (int) (Math.random() * 44) + 6;

    /**
     * new entry
     */
    private Entry entry;
    /**
     * List with all entrys
     */
    private ArrayList<Entry> doorsAndStairs = new ArrayList<Entry>();
    /**
     * random roomType
     */
    private final int ROOMTYPE = (int) (Math.random() * 100);

    /**
     * Difficulty
     */
    private final Difficulty DIFFICULTY;

    /**
     * List of all Enemies
     */
    private ArrayList<Enemy> enemies;

    /**
     * List of all Items
     */
    private ArrayList<Potion> items;

    /**
     * List of all Coins
     */
    private ArrayList<Coin> coins;

    /**
     * FieldType is the main array in which the end Field will be saved.
     */
    private FieldType[][] room = new FieldType[HIGHT][WIDTH];

    /**
     * Constructor to Field.
     *
     * @param entry is the new direction for entry position.
     */
    public Room(Difficulty difficulty, Entry entry) {

        //Feeding constructor needed infos
        this.DIFFICULTY = difficulty;
        this.entry = entry;

        //Fill room-array with simple wall and floor
        new CreateWallAndFloor(room);

        //Selection of roomtypes
        if(ROOMTYPE <= 100 && ROOMTYPE >= 0) {
            //Adding entry and exit
            new PutEntryAndExit(entry, room, doorsAndStairs);
        //ToDo: Different map genertors
        }

        //Prerefresh to make the room usable for other logics
        refreshRoom();

        //Create Enemys
        enemies = CreateEnemies.fillEnemyList(room);

        //Prerefresh to make the room usable for other logics
        refreshRoom();

        //Create Items
        items = CreateItems.fillItemList(room);

        //Prerefresh to make the room usable for other logics
        refreshRoom();

        //Set Coins;
        coins = SetCoins.fillCoinList(room);

        //Prerefresh to make the room usable for other logics
        refreshRoom();
    }

    /**
     * Refresh room with new Player, Enemy, Item and Coin Coordinates
     */
    private void refreshRoom() {

        //Reset all floor tiles back to FieldType.FLOOR
        for(int width = 1; width < room.length - 1; width++) {
            for(int hight = 1; hight < room[0].length - 1; hight++) {
                if(room[width][hight] == FieldType.Enemy || room[width][hight] == FieldType.ItemField || room[width][hight] == FieldType.CoinField || room[width][hight] == FieldType.Player) {
                    room[width][hight] = FieldType.Floor;
                }
            }
        }

        //Adding item tiles
        if(!(items == null)) {
            for (int itemcount = 0; itemcount < items.size(); itemcount++) {
                room[items.get(itemcount).getCoordinate().getyCoordinate()][items.get(itemcount).getCoordinate().getxCoordinate()] = FieldType.ItemField;
            }
        }

        //Adding coin tiles
        if(!(coins == null)) {
            for (int coinCount = 0; coinCount < coins.size(); coinCount++) {
                room[coins.get(coinCount).getCoordinate().getyCoordinate()][coins.get(coinCount).getCoordinate().getxCoordinate()] = FieldType.CoinField;
            }
        }

        //Adding enemy tiles
        if(!(enemies == null)) {
            for (int enemycount = 0; enemycount < enemies.size(); enemycount++) {
                room[enemies.get(enemycount).GetCoordinate().getyCoordinate()][enemies.get(enemycount).GetCoordinate().getxCoordinate()] = FieldType.Enemy;
            }
        }

        //Refreshing door and stairs tiles
        for (int entrycount = 0; entrycount < doorsAndStairs.size(); entrycount++) {
            room[doorsAndStairs.get(entrycount).getCoordinate().getyCoordinate()][doorsAndStairs.get(entrycount).getCoordinate().getxCoordinate()] = doorsAndStairs.get(entrycount).getFieldType();
        }

        //Adding player tile
        room[Field.getPlayer().GetCoordinate().getyCoordinate()][Field.getPlayer().GetCoordinate().getxCoordinate()] = FieldType.Player;
    }

    /**
     * Method to trigger other mechanics
     */
    public void checkPlayerField() {

        //Let the enemies move
        for(Enemy enemy : enemies) {
            enemy.NextMove();
            refreshRoom();
        }

        //Integer to keep track of the coin which is used
        int coinCounter = 0;

        //Integer to keep track of the item which is used
        int itemCounter = 0;

        //Integer to keep track of the entry which is used
        int entryCounter = 0;

        //Integer to keep track of the enemy which is used
        int enemyConter = 0;

        //Checks if enemy stands on a coin
        boolean playerTileEquelsCoinTile = false;
        for(int coin = 0; coin < coins.size(); coin++) {
            if(Field.getPlayer().GetCoordinate().equals(coins.get(coin).getCoordinate())) {
                playerTileEquelsCoinTile = true;
                coinCounter = coin;
            }
        }

        //Checks if enemy stands on a item
        boolean playerTileEquelsItemTile = false;
        for(int item = 0; item < items.size(); item++) {
            if(Field.getPlayer().GetCoordinate().equals(items.get(item).getCoordinate())) {
                playerTileEquelsItemTile = true;
                itemCounter = item;
            }
        }

        //Checks if enemy stands on a entry
        boolean playerTileEquelsEntryTile = false;
        for(int entry = 0; entry < doorsAndStairs.size(); entry++) {
            if(Field.getPlayer().GetCoordinate().equals(doorsAndStairs.get(entry).getCoordinate())) {
                playerTileEquelsEntryTile = true;
                entryCounter = entry;
            }
        }

        //Checks if enemy stands on a enemy
        boolean playerTileEquelsEnemyTile = false;
        for(int enemy = 0; enemy < enemies.size(); enemy++) {
            if(Field.getPlayer().GetCoordinate().equals(enemies.get(enemy).GetCoordinate()) ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1 ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1 ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() ||
               Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
               Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1) {
                playerTileEquelsEnemyTile = true;
                enemyConter = enemy;
            }
        }

        //Trigger coin logic
        if(playerTileEquelsCoinTile){
            Field.getInventory().addCoin(coins.get(coinCounter));
            coins.remove(coinCounter);
        }

        //Trigger item logic
        if(playerTileEquelsItemTile && Inventory.hasFreeSlot()){
            Field.getInventory().addPotion(items.get(itemCounter));
            items.remove(itemCounter);
        }

        //Trigger entry logic
        if(playerTileEquelsEntryTile){
            switch(doorsAndStairs.get(entryCounter).getDirection()) {
                case Top:
                    if(doorsAndStairs.get(entryCounter).isEntrance()) {
                        Field.getPlayer().SetCoordinate(new Coordinate(doorsAndStairs.get(entryCounter).getCoordinate().getxCoordinate(), doorsAndStairs.get(entryCounter).getCoordinate().getyCoordinate() + 1));

                    }else {
                        entry = new Entry(doorsAndStairs.get(entryCounter).getCoordinate(), Directions.Bottom, true, FieldType.Door);
                        Field.setRoom(new Room(Difficulty.Medium, getEntry()));
                    }
                    break;
                case Right:
                    if(doorsAndStairs.get(entryCounter).isEntrance()) {
                        Field.getPlayer().SetCoordinate(new Coordinate(doorsAndStairs.get(entryCounter).getCoordinate().getxCoordinate() - 1, doorsAndStairs.get(entryCounter).getCoordinate().getyCoordinate()));
                    }else {
                        entry = new Entry(doorsAndStairs.get(entryCounter).getCoordinate(), Directions.Left, true, FieldType.Door);
                        Field.setRoom(new Room(Difficulty.Medium, getEntry()));
                    }
                    break;
                case Bottom:
                    if(doorsAndStairs.get(entryCounter).isEntrance()) {
                        Field.getPlayer().SetCoordinate(new Coordinate(doorsAndStairs.get(entryCounter).getCoordinate().getxCoordinate(), doorsAndStairs.get(entryCounter).getCoordinate().getyCoordinate() - 1));
                    }else {
                        entry = new Entry(doorsAndStairs.get(entryCounter).getCoordinate(), Directions.Top, true, FieldType.Door);
                        Field.setRoom(new Room(Difficulty.Medium, getEntry()));
                    }
                    break;
                case Left:
                    if(doorsAndStairs.get(entryCounter).isEntrance()) {
                        Field.getPlayer().SetCoordinate(new Coordinate(doorsAndStairs.get(entryCounter).getCoordinate().getxCoordinate() + 1, doorsAndStairs.get(entryCounter).getCoordinate().getyCoordinate()));
                    }else {
                        entry = new Entry(doorsAndStairs.get(entryCounter).getCoordinate(), Directions.Right, true, FieldType.Door);
                        Field.setRoom(new Room(Difficulty.Medium, getEntry()));
                    }
                    break;
                default:
                    if(doorsAndStairs.get(entryCounter).isEntrance()) {
                        Field.getPlayer().SetCoordinate(new Coordinate(doorsAndStairs.get(entryCounter).getCoordinate().getxCoordinate() - 1, doorsAndStairs.get(entryCounter).getCoordinate().getyCoordinate()));
                    }else {
                        entry = new Entry(doorsAndStairs.get(entryCounter).getCoordinate(), Directions.NotDefined, true, FieldType.Stairs);
                        Field.setRoom(new Room(Difficulty.Medium, getEntry()));
                    }
                    break;
            }

            //Trigger enemy logic
        }else if(playerTileEquelsEnemyTile) {
            while(playerTileEquelsEnemyTile == true) {
                Field.getPlayer().SetHealthPoints(Field.getPlayer().GetHealthPoints() - 10);
                enemies.remove(enemyConter);
                playerTileEquelsEnemyTile = false;
                for(int enemy = 0; enemy < enemies.size(); enemy++) {
                    if(Field.getPlayer().GetCoordinate().equals(enemies.get(enemy).GetCoordinate()) ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1 ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1 ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() + 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() + 1 ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() ||
                        Field.getPlayer().GetCoordinate().getxCoordinate() == enemies.get(enemy).GetCoordinate().getxCoordinate() - 1 &&
                        Field.getPlayer().GetCoordinate().getyCoordinate() == enemies.get(enemy).GetCoordinate().getyCoordinate() - 1) {
                        playerTileEquelsEnemyTile = true;
                        enemyConter = enemy;
                    }
                }
            }

        }
    }


    /**
     * Passes on the EntryDirection for the next field.
     *
     * @return dirction of the new entry.
     */
    public Entry getEntry() {
        return entry;
    }


    /**
     * Returns FieldType for Field-, Player-interactions or rendering.
     *
     * @return a specific FieldType.
     */
    public FieldType[][] getRoom() {
        refreshRoom();
        return room;
    }

    /**
     * Returns a requested FieldType
     *
     * @param xCoordinate of requested tile
     * @param yCoordinate of requested tile
     * @return FieldType of requested tile
     */
    public FieldType getFieldType(int xCoordinate, int yCoordinate) {
        refreshRoom();
        return room[yCoordinate][xCoordinate];
    }
}
