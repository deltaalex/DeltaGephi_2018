/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin.social.diffusion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.SNode;
import org.openide.util.Exceptions;

/**
 *
 * @author Alexander
 *
 * Implements the super class of all diffusion models
 */
public abstract class AbstractDiffusionImpl extends Thread {

    protected HierarchicalGraph graph;
    private boolean animate;
    private int animationDelay;
    private boolean running;
    protected Random random;

    public AbstractDiffusionImpl(HierarchicalGraph graph, boolean animate, int animationDelay) {
        this.graph = graph;
        this.animate = animate;
        this.animationDelay = animationDelay;
        running = false;

        random = new Random();
    }

    public abstract float interact(SNode snode);

    @Override
    public void run() {
        running = true;
        // counts the number of flipped opinions during each cycle
        int modifications = Integer.MAX_VALUE;
        // stops when less than p% nodes flip opinion in one cycle
        float stopPercentage = 0.01f;
        float myOpinion, myOldOpinion;
        // default node size
        float nodeSize = 5f;
        // flag stubborn agents
        int stubborn = 0;
        // polling
        int nneg, npos;

        int count = 0;
        // runs until the modification ratio is smaller than p%
        try {
            graph.writeLock();
            while (running) {
                //while ((float) (modifications / n) > stopPercentage && !isCanceled) {
                // reset at each cylce
                modifications = 0;
                nneg = 0;
                npos = 0;

                // iterate graph
                for (Node node : graph.getNodes()) {
                    SNode snode = new SNode(node);

                    // omit if stubborn
                    stubborn = snode.getValueAsInteger(SNode.Stubborn);
                    // if first cycle and is stubborn
                    if (stubborn > 0) {
                        if (count == 0) {
                            // increase size permanentely for this run                            
                            snode.setStubbornSize();
                            // color according to edited opinion
                            myOpinion = snode.getValueAsFloat(SNode.Opinion);
                            snode.setColor(1 - myOpinion, myOpinion, 0);
                        }
                        continue;
                    } else {
                        if (count == 0) {
                            snode.resetSize();
                        }
                    }

                    // get sleep state and update
                    int sleep = snode.getValueAsInteger(SNode.Sleep);
                    sleep--;

                    // interact if awake
                    if (sleep <= 0) {
                        // highlight node
                        if (animate) {
                            snode.setActiveSize();
                        }

                        // save initial state
                        myOpinion = snode.getValueAsFloat(SNode.Opinion);
                        myOldOpinion = myOpinion;

                        // implementing subclass
                        myOpinion = interact(snode);

                        // poll society
                        if (myOpinion < 0.5) {
                            nneg++;
                        } else {
                            npos++;
                        }

                        snode.setValueAsFloat(SNode.Opinion, myOpinion);
                        snode.setColor(1 - myOpinion, myOpinion, 0);

                        // count modification
                        if (myOldOpinion != myOpinion) {
                            modifications++;
                        }

                        // reset sleep interval after each interaction
                        snode.setSleep();

                        // animate - delay
                        if (animate) {
                            try {
                                Thread.sleep(animationDelay);
                            } catch (InterruptedException e) {
                            }
                            // reset size after animation delay
                            snode.resetSize();
                        }

                    } // ignore node if asleep
                    else {
                        snode.setSleep(sleep);
                    }

                    // check stop
                    if (!running) {
                        break;
                    }

                    if (count % 100 == 0) {
                        polls.add(runPoll(graph));
                    }
                }
                count++;

                // check stop
                if (!running) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // !!! unlock so other may use the graph
            graph.writeUnlock();
            graph.readUnlock();
        }
    }
    private List<Double> polls = new ArrayList<Double>();

    public void stopRunning() {
        running = false;

        try {
            File tmp = new File(System.getProperty("user.home") + "/Desktop/tolerance.txt");
            PrintWriter pw = new PrintWriter(tmp);
            for (double p : polls) {
                pw.println(p);
            }

            pw.close();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected <T> T selectRandom(final Iterator<T> iter, final Random random) {
        if (!iter.hasNext()) {
            return null;
        }
        if (random == null) {
            throw new NullPointerException();
        }
        T selected = iter.next();
        int count = 1;
        while (iter.hasNext()) {
            final T current = iter.next();
            ++count;
            if (random.nextInt(count) == 0) {
                selected = current;
            }
        }
        return selected;
    }

    private double runPoll(HierarchicalGraph graph) {
        // measure the raio of same vs different opinion for each node taking into consideration all direct neighbors.
        int foundSame = 0, foundDifferent = 0;

        for (int i = 0; i < graph.getNodeCount(); ++i) {
            SNode source = new SNode(graph.getNodes().toArray()[i]);
            Float opinion = source.getValueAsFloat(SNode.Opinion);

            Node[] neighbors = graph.getNeighbors(source.getNode()).toArray();
            for (Node n : neighbors) {
                SNode neighbor = new SNode(n);
                Float opinion2 = neighbor.getValueAsFloat(SNode.Opinion);

                if ((opinion - 0.5f) * (opinion2 - 0.5f) > 0) // same
                {
                    foundSame++;
                } else {
                    foundDifferent++;
                }
            }
        }

        return 1.0 * foundSame / (foundSame + foundDifferent);
    }
}