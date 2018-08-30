package ru.minesweeper.storage.game;

import org.springframework.stereotype.Repository;
import ru.minesweeper.viewmodel.GameProperties;
import ru.minesweeper.logic.UnauthGame;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository("UnauthGames")
public class UnauthGameStorage implements GameStorage<String> {
    public UnauthGameStorage() {}

    private long clearTime = 900_000;

    private Thread finishedGamesCollector;

    private Map<String,UnauthGame> games = new ConcurrentHashMap<>();

    @Override
    public UnauthGame getGame(String id) {
        return games.get(id);
    }

    @Override
    public String createGame(GameProperties properties) {
        UnauthGame game = new UnauthGame(properties);
        String id = UUID.randomUUID().toString();
        games.put(id,game);
        if(finishedGamesCollector==null || !finishedGamesCollector.isAlive()){
            finishedGamesCollector = new Thread( ()->{
                while (games.size()>0){
                    try {
                        Thread.sleep(getClearTime());
                    } catch (InterruptedException e) {
                        break;
                    }
                    for (Iterator<Map.Entry<String, UnauthGame>> it = games.entrySet().iterator();
                         it.hasNext();)
                    {
                        Map.Entry<String, UnauthGame> entry = it.next();
                        UnauthGame g = entry.getValue();
                        if(g.isFinished()){it.remove();}
                    }
                }
            });
            finishedGamesCollector.setDaemon(true);
            finishedGamesCollector.start();
        }
        return id;
    }

    @Override
    public void removeGame(String id) {
        games.remove(id);
    }

    public long getClearTime() {
        return clearTime;
    }

    public void setClearTime(long clearTime) {
        this.clearTime = clearTime;
    }
}
