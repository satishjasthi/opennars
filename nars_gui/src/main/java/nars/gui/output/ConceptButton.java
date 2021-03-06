/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package nars.gui.output;

import nars.NAR;
import nars.concept.Concept;
import nars.term.Term;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** represents either a Term or its Concept (if exists).
if a concept is involved, there may be additional data to display.     */
public class ConceptButton extends JButton implements ActionListener {

    private Concept concept;
    private final Term term;
    private NAR nar;

    public ConceptButton(NAR n, Term t) {
        super(t.toString());
        this.term = t;
        this.nar = n;
        addActionListener(this);
    }

    public ConceptButton(NAR n, Concept c) {
        this(n, c.getTerm());
        this.concept = c;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (this.concept == null) {
            this.concept = nar.memory.concept(term);
        }
        if (this.concept == null) {
            //concept doesnt exist (yet)
            return;
        }
        //popup(nar, concept);
    }
//
//    public static void popup(NAR nar, Concept concept) {
//        ConceptPanelBuilder cp;
//        NWindow w = new NWindow(concept.term.toString(), new JScrollPane(cp = new ConceptPanelBuilder(nar, concept)));
//        cp.onShowing(true);
//        w.pack();
//        w.setVisible(true);
//    }
//
}
