/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.maciejkamilplayer;

import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class MaciejKamilPlayer extends Player {

    private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Maciej Wieczorek 148141 Kamil Stachowiak XXXXXX";
    }


    @Override
    public Move nextMove(Board b) {
        List<Move> moves = b.getMovesFor(getColor());
        return moves.get(random.nextInt(moves.size()));
    }
}
