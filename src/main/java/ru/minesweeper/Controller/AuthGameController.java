package ru.minesweeper.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.minesweeper.logic.AuthGame.AuthGame;
import ru.minesweeper.logic.AuthGame.GameCycle;
import ru.minesweeper.logic.AuthGame.GameInvitation;
import ru.minesweeper.logic.AuthGame.Listeners.Sse.SseBeforeGameListener;
import ru.minesweeper.logic.AuthGame.Listeners.Sse.SseGameEventsListener;
import ru.minesweeper.logic.AuthGame.Listeners.Sse.SseInviteListener;
import ru.minesweeper.logic.AuthGame.Listeners.Sse.SseJoinListener;
import ru.minesweeper.logic.Cell;
import ru.minesweeper.model.User;
import ru.minesweeper.service.invitation.InvitationsService;
import ru.minesweeper.storage.JpaUserStorage;
import ru.minesweeper.storage.game.AuthGameStorage;
import ru.minesweeper.viewmodel.*;
import ru.minesweeper.viewmodel.cell.CellVM;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/AuthGame")
public class AuthGameController {
    private final AuthGameStorage<Integer, String> gameStorage;
    private final JpaUserStorage userStorage;
    private final InvitationsService<Integer, String> invitationService;

    @Autowired
    public AuthGameController(@Qualifier("AuthGames") AuthGameStorage<Integer, String> gameStorage, JpaUserStorage userStorage, InvitationsService<Integer, String> invitationService) {
        this.gameStorage = gameStorage;
        this.userStorage = userStorage;
        this.invitationService = invitationService;
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.GET)
    public String GameStart(Model model) {
        model.addAttribute("gameType", "AuthGame");
        return "GameStartForm";
    }

    @RequestMapping(value = "/GameStart", method = RequestMethod.POST)
    public String GameStart(@ModelAttribute GameProperties gameProperties) {
        User owner = getCurrentUser();
        //ToDo: удалить старую игру
        owner.setCurrentGameId(gameStorage.createGame(owner.getId(), gameProperties));
        userStorage.save(owner);
        return "redirect:/AuthGame/Game";
    }

    @RequestMapping("/Game")
    public String Game() {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            return "redirect:/AuthGame/GameStart";
        }
        return "redirect:/AuthGame/Game/" + currentUser.getCurrentGameId();
    }

    @RequestMapping("/Game/{id}")
    public ModelAndView Game(@PathVariable("id") String gameId, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(gameId);
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (!gameCycle.getPlayers().contains(currentUser.getId()) && gameCycle.getStage() != GameCycle.Stage.INVITATION) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        switch (gameCycle.getStage()) {
            case INVITATION:
                invitationStage(modelAndView, gameId, currentUser, gameCycle);
                break;
            case CONNECTIONS_GATHERING:
                connectionsGatheringStage(modelAndView, currentUser, gameCycle);
                break;
            case GAME:
                gameStage(modelAndView, currentUser, gameCycle);
                break;
        }
        return modelAndView;
    }

    private void invitationStage(ModelAndView modelAndView, String gameId, User currentUser, GameCycle<Integer, String> gameCycle) {
        modelAndView.setViewName("Invitation");
        modelAndView.addObject("properties", gameCycle.getGameProperties());
        modelAndView.addObject("gameId", gameId);
        modelAndView.addObject("isOwner", gameCycle.getOwner().equals(currentUser.getId()));
        modelAndView.addObject("owner", gameCycle.getOwner());
        GameInvitation<Integer, String> invitation = gameCycle.getInvitations();
        modelAndView.addObject("isOwnerConfirmed", invitation.isOwnerConfirmed(currentUser.getId()));
        modelAndView.addObject("isPlayerConfirmed", invitation.isPlayerConfirmed(currentUser.getId()));
        ArrayList<InvitationStatus> players = new ArrayList<>();
        for (Integer playerId : invitation.getPlayers()) {
            Optional<User> player = userStorage.findById(playerId);
            if (!player.isPresent()) continue;
            players.add(new InvitationStatus(
                    playerId, player.get().getUserName(),
                    invitation.isPlayerConfirmed(playerId),
                    invitation.isOwnerConfirmed(playerId)));
        }
        modelAndView.addObject("players", players);
    }

    private void connectionsGatheringStage(ModelAndView modelAndView, User currentUser, GameCycle<Integer, String> gameCycle) {
        modelAndView.addObject("properties", gameCycle.getGameProperties());
        modelAndView.addObject("userId", currentUser.getId());
        ArrayList<PlayerConnected> connections = new ArrayList<>();
        for (Integer playerId : gameCycle.getPlayers()) {
            Optional<User> player = userStorage.findById(playerId);
            if (!player.isPresent()) continue;
            connections.add(new PlayerConnected(
                    playerId,
                    player.get().getUserName(),
                    gameCycle.getConnectionsGathering().isConnected(playerId)));
        }
        modelAndView.addObject("connections", connections);
        modelAndView.setViewName("ConnectionsGathering");
    }

    private void gameStage(ModelAndView modelAndView, User currentUser, GameCycle<Integer, String> gameCycle) {
        AuthGame<Integer> game = gameCycle.getGame();
        Cell[][] field = game.getField();
        modelAndView.addObject("userId", currentUser.getId());
        modelAndView.addObject("field", field);
        modelAndView.addObject("bombsLeft", game.getBombsLeft());
        modelAndView.addObject("properties", game.getProperties());
        modelAndView.addObject("isFinished", game.isFinished());
        modelAndView.addObject("isStarted", game.isStarted());
        ArrayList<UserVM> players1 = new ArrayList<>();
        for (ScoreModel<Integer> score : game.getScores()) {
            User player = userStorage.findById(score.getPlayer()).orElse(null);
            if (player == null) continue;
            players1.add(new UserVM(
                    player.getId(),
                    player.getUserName(),
                    player.getRealName()));
        }
        modelAndView.addObject("players1", players1);
        modelAndView.addObject("scores", new ArrayList<>(game.getScores()));
        modelAndView.addObject("winner", game.getWinners());
        modelAndView.setViewName("AuthGame");
    }

    @RequestMapping(value = "/invite/{player}", method = RequestMethod.GET)
    public @ResponseBody
    InvitationStatus invite(@PathVariable String player, HttpServletResponse response) {
        User toInvite = userStorage.findUserByUserName(player);
        if (toInvite == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> game = gameStorage.getGame(currentUser.getCurrentGameId());
        if (game.getStage() != GameCycle.Stage.INVITATION || game.getOwner() != currentUser.getId()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameInvitation<Integer, String> invitations = game.getInvitations();
        invitations.invitePlayer(toInvite.getId());
        return new InvitationStatus(toInvite.getId(), toInvite.getUserName(),
                invitations.isPlayerConfirmed(toInvite.getId()), invitations.isOwnerConfirmed(toInvite.getId()));
    }

    @RequestMapping("/join/{id}")
    public String joinGame(@PathVariable String id, HttpServletResponse response) {
        GameCycle<Integer, String> game = gameStorage.getGame(id);
        if (game == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (game.getStage() != GameCycle.Stage.INVITATION) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        User currentUser = getCurrentUser();
        if (currentUser.getCurrentGameId() != null) {
            gameStorage.getGame(currentUser.getCurrentGameId()).leave(currentUser.getId());
        }
        game.getInvitations().joinGame(currentUser.getId());
        currentUser.setCurrentGameId(id);
        userStorage.save(currentUser);
        return "redirect:/AuthGame/Game/" + id;
    }

    @RequestMapping("/Invitations")
    public ModelAndView invitations() {
        ModelAndView modelAndView = new ModelAndView("InvitationsList");
        User currentUser = getCurrentUser();
        ArrayList<String> invitationIds = new ArrayList<>(invitationService.getInvitationsCount(currentUser.getId()));
        ArrayList<GameProperties> invitationGames = new ArrayList<>(invitationService.getInvitationsCount(currentUser.getId()));
        ArrayList<UserVM> gamesOwners = new ArrayList<>(invitationService.getInvitationsCount(currentUser.getId()));
        for (GameCycle<Integer, String> game : invitationService.getInvitations(currentUser.getId())) {
            invitationIds.add(gameStorage.getGameId(game));
            invitationGames.add(game.getGameProperties());
            User owner = userStorage.findById(game.getOwner()).orElse(null);
            if (owner == null) continue;
            gamesOwners.add(new UserVM(owner.getId(), owner.getUserName(), owner.getRealName()));
        }
        modelAndView.addObject("invitationIds", invitationIds);
        modelAndView.addObject("gamesOwners", gamesOwners);
        modelAndView.addObject("invitationGames", invitationGames);
        return modelAndView;
    }

    @RequestMapping("/InvitationListener")
    public SseEmitter invitationsListener() {
        User currentUser = getCurrentUser();
        SseEmitter emitter = new SseEmitter(-1L);
        invitationService.setInvitationListener(currentUser.getId(), new SseInviteListener<>(emitter));
        return emitter;
    }

    private User getCurrentUser() {
        return userStorage.findUserByUserName((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @RequestMapping("/JoinListener")
    public SseEmitter joinListener(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        if (currentUser.getCurrentGameId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        GameCycle<Integer, String> game = gameStorage.getGame(currentUser.getCurrentGameId());
        if (game == null) {
            currentUser.setCurrentGameId(null);
            userStorage.save(currentUser);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (!Objects.equals(game.getOwner(), currentUser.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        if (game.getStage() != GameCycle.Stage.INVITATION) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        game.getInvitations().setJoinListener(new SseJoinListener(emitter, userStorage));
        return emitter;
    }

    @RequestMapping(value = "/CompleteInvitation", method = RequestMethod.GET)
    public String completeInvitation(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle.getStage() == GameCycle.Stage.INVITATION) {
            if (gameCycle.getInvitations().getOwner().equals(currentUser.getId())) {
                gameCycle.nextStage();
                return "redirect:/AuthGame/Game";
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return null;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
    }

    @RequestMapping(value = "/BeforeGameListener")
    public SseEmitter beforeGameListener(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (gameCycle.getStage() != GameCycle.Stage.INVITATION) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        gameCycle.getInvitations().setListener(currentUser.getId(), new SseBeforeGameListener(emitter, userStorage));
        return emitter;
    }

    @RequestMapping(value = "/ConnectedPlayers")
    public @ResponseBody
    Collection<PlayerConnected> getPlayersConnected(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (gameCycle.getStage() != GameCycle.Stage.CONNECTIONS_GATHERING) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (!gameCycle.getPlayers().contains(currentUser.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        ArrayList<PlayerConnected> connections = new ArrayList<>();
        for (Integer playerId : gameCycle.getPlayers()) {
            User player = userStorage.findById(playerId).orElse(null);
            if (player == null) continue;
            connections.add(new PlayerConnected(playerId, player.getUserName(), gameCycle.getConnectionsGathering().isConnected(playerId)));
        }
        return connections;
    }

    @RequestMapping(value = "/GameListener")
    public SseEmitter gameListener(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (gameCycle.getStage() == GameCycle.Stage.INVITATION) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        if (!gameCycle.getPlayers().contains(currentUser.getId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        SseEmitter emitter = new SseEmitter(-1L);
        if (gameCycle.getStage() == GameCycle.Stage.CONNECTIONS_GATHERING) {
            gameCycle.getConnectionsGathering().setConnection(currentUser.getId(), new SseGameEventsListener<>(emitter));
        } else {
            gameCycle.getGame().setListener(currentUser.getId(), new SseGameEventsListener<>(emitter));
        }
        return emitter;
    }

    @RequestMapping(value = "/OpenCell", method = RequestMethod.POST)
    public void openCell(@ModelAttribute CellVM cell, HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (gameCycle.getStage() != GameCycle.Stage.GAME) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (gameCycle.getGame().isFinished()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        gameStorage.getGame(currentUser.getCurrentGameId()).getGame().openCell(cell.getX(), cell.getY(), currentUser.getId());
    }

    @RequestMapping(value = "/SuggestBomb", method = RequestMethod.POST)
    public void suggestBomb(@ModelAttribute CellVM cell, HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> gameCycle = gameStorage.getGame(currentUser.getCurrentGameId());
        if (gameCycle == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (gameCycle.getStage() != GameCycle.Stage.GAME) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (gameCycle.getGame().isFinished()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        gameStorage.getGame(currentUser.getCurrentGameId()).getGame().suggestBomb(cell.getX(), cell.getY(), currentUser.getId());
    }

    @RequestMapping("/GetScores")
    public @ResponseBody
    Collection<ScoreModel<Integer>> getScores(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> game = gameStorage.getGame(currentUser.getCurrentGameId());
        if (game == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (game.getStage() != GameCycle.Stage.GAME) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return gameStorage.getGame(currentUser.getCurrentGameId()).getGame().getScores();
    }

    @RequestMapping("/Score")
    public @ResponseBody
    Integer checkScore(HttpServletResponse response) {
        User currentUser = getCurrentUser();
        GameCycle<Integer, String> game = gameStorage.getGame(currentUser.getCurrentGameId());
        if (game == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (game.getStage() != GameCycle.Stage.GAME) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return gameStorage.getGame(currentUser.getCurrentGameId()).getGame().getScore(currentUser.getId());
    }

    @RequestMapping("/Score/{playerId}")
    public @ResponseBody
    Integer checkScore(@PathVariable Integer playerId, HttpServletResponse response) {
        User player = userStorage.findById(playerId).orElse(null);
        if (player == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (player.getCurrentGameId() == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        User currentUser = getCurrentUser();
        if (!Objects.equals(player.getCurrentGameId(), currentUser.getCurrentGameId())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }
        GameCycle<Integer, String> game = gameStorage.getGame(player.getCurrentGameId());
        if (game == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        if (game.getStage() != GameCycle.Stage.GAME) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return null;
        }
        return game.getGame().getScore(playerId);
    }


}