declare module "mxgraph" {

  /**
   * mxGraph is a for-real pain to bundle correctly for Vaadin consumption
   * so the strategy is to supply the client bundle
   * and export all the correct types
   */

  global {

    /**
     *
     */
    export type mxGraph = typeof mxGraph;

    /**
     *
     */
    export type mxClient = typeof mxClient;

    /**
     * we're just attaching mxGraph on the window object--it works
     * better this way
     */
        // @ts-ignore
    const mxGraph: typeof mxGraph;
    // @ts-ignore
    const mxClient: typeof mxClient;

    // @ts-ignore
    const mxGraphModel: typeof mxGraphModel;

    // @ts-ignore
    const mxUtils: typeof mxUtils;
    // @ts-ignore
    const mxEvent: typeof mxEvent;
    // @ts-ignore
    const mxRubberband: typeof mxRubberband;
  }


}


