/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.maciejkamilplayer;

import java.util.*;

import put.ai.games.engine.BoardFactory;
import put.ai.games.engine.Callback;
import put.ai.games.engine.GameEngine;
import put.ai.games.engine.impl.GameEngineImpl;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;
import put.ai.games.game.exceptions.RuleViolationException;
import put.ai.games.naiveplayer.NaivePlayer;
import put.ai.games.rulesprovider.RulesProvider;

public class MaciejKamilPlayer extends Player {

    private Random random = new Random(0xdeadbeef);

    @Override
    public String getName() {
        return "Maciej Wieczorek 148141 Kamil Stachowiak XXXXXX";
    }

    int heuristicFunction(State s)
    {
        int myMoves = s.getMoves().size();
        int enemyMoves = s.board.getMovesFor(getEnemyPlayer(s.player)).size();
        return myMoves;
    }

    boolean isTerminalNode(State s)
    {
        return s.getMoves().size() == 0;
    }

    int AlphaBeta(State s, int depth, int alpha, int beta, Color type)
    {
        if (isTerminalNode(s) || depth == 0)
            return s.getHeuristicValue();
        if (type == getColor())
        {
            for (State child : s.getSuccessors())
            {
                int val = AlphaBeta(child, depth-1, alpha, beta, getEnemyPlayer(type));
                alpha = val > alpha ? val : alpha;
                if (alpha >= beta) return beta;
            }
            return alpha;
        }
        else
        {
            for (State child : s.getSuccessors())
            {
                int val = AlphaBeta(child, depth-1, alpha, beta, getEnemyPlayer(type));
                beta = val < beta ? val : beta;
                if (alpha >= beta) return alpha;
            }
            return beta;
        }
    }

    Color getEnemyPlayer(Color player)
    {
        return player == Color.PLAYER1 ? Color.PLAYER2 : Color.PLAYER1;
    }

    class State
    {
        public Board board;
        public Color player;
        private ArrayList<State> successors = null;
        private List<Move> moves = null;
        private int heuristicValue = Integer.MIN_VALUE;

        public State(Board board, Color player)
        {
            this.board = board;
            this.player = player;
            this.moves = this.board.getMovesFor(this.player);
        }

        private void generateSuccessors()
        {
            successors = new ArrayList<State>();
            for (Move move : getMoves())
            {
                Board boardAfterMove = board.clone();
                boardAfterMove.doMove(move);
                successors.add(new State(boardAfterMove, getEnemyPlayer(player)));
            }
        }
        private void generateMoves()
        {
            moves = board.getMovesFor(player);
        }

        public List<State> getSuccessors()
        {
            if (successors == null)
                generateSuccessors();
            return successors;
        }

        public List<Move> getMoves()
        {
            if (moves == null)
                generateMoves();
            return moves;
        }

        public int getHeuristicValue()
        {
            if (heuristicValue == Integer.MIN_VALUE)
                heuristicValue = heuristicFunction(this);
            return heuristicValue;
        }

    }


    @Override
    public Move nextMove(Board b) {
        Color myColor = getColor();
        State currentState = new State(b.clone(), getColor());
        int bestMove = 0;
        int minVal = Integer.MAX_VALUE;
        for (int i = 0; i < currentState.getSuccessors().size(); i++)
        {
            int val = AlphaBeta(currentState.getSuccessors().get(i), 2, Integer.MIN_VALUE, Integer.MAX_VALUE, getEnemyPlayer(getColor()));
            if (val < minVal)
            {
                minVal = val;
                bestMove = i;
            }
        }
        System.out.println(minVal);

        List<Move> myMoves = currentState.getMoves();
        return myMoves.get(bestMove);
    }

    public static void main(String[] a)
    {
        int boardSize = 8;

        BoardFactory boardFactory = RulesProvider.INSTANCE.getRulesByName("Migration");

        Map<String, Object> config = new HashMap<>();
        config.put(BoardFactory.BOARD_SIZE, boardSize);
        boardFactory.configure(config);
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        GameEngine g = new GameEngineImpl(boardFactory);
        String result = "";

        g.addPlayer(new MaciejKamilPlayer());
        g.addPlayer(new NaivePlayer());

        String error = "";
        Color winner;
        try {
            winner = g.play(new Callback() {

                @Override
                public void update(Color c, Board b, Move m) {

                }
            });
        } catch (RuleViolationException ex) {
            winner = Player.getOpponent(ex.getGuilty());
        }
        System.out.println(winner);
    }
}
