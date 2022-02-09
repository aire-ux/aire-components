import {JQuery, View} from '@antv/x6';

View.prototype.getEventTarget = function getEventTarget(
    e: JQuery.TriggeredEvent,
    options: { fromPoint?: boolean } = {},
): unknown {
  const {type, clientX = 0, clientY = 0} = e;
  if (options.fromPoint || type === 'touchmove' || type === 'touchend' || type === 'hover') {
    let rootNode: HTMLDocument | ShadowRoot | undefined;
    if (this.container && 'getRootNode' in this.container) {
      rootNode = this.container.getRootNode() as HTMLDocument | ShadowRoot;
    }
    if (!rootNode || !('elementFromPoint' in rootNode)) {
      rootNode = document;
    }
    return rootNode.elementFromPoint(clientX, clientY);
  }

  return e.target as unknown;
};

View.prototype.normalizeEvent = function <T extends JQuery.TriggeredEvent>(evt: T) {
  return normalizeEvent(evt, this.container);
};

const _normalizeEvent = View.normalizeEvent;
View.normalizeEvent = normalizeEvent;

function normalizeEvent<T extends JQuery.TriggeredEvent>(event: T, element?: Element): T {
  event = _normalizeEvent(event);
  let original: Event & JQuery.TriggeredEvent = event as unknown as Event & JQuery.TriggeredEvent;
  while (original.originalEvent) {
    original = original.originalEvent as Event & JQuery.TriggeredEvent;
  }
  if (original.composed) {
    const path = original.composedPath();
    let start = path.length - 1;
    if (element) {
      const root = element.getRootNode();
      if (root instanceof DocumentFragment) {
        const fixedStart = path.lastIndexOf(root);
        if (fixedStart > 0) start = fixedStart;
      }
    }
    let containerIndex = -1,
        fragmentIndex = -1;
    for (let i = start; i >= 0; i--) {
      const el = path[i];
      if (containerIndex < 0) {
        if (el instanceof HTMLElement && el.classList.contains('x6-graph')) {
          containerIndex = i;
          continue;
        }
      } else if (fragmentIndex < 0) {
        if (el instanceof DocumentFragment) {
          fragmentIndex = i;
          break;
        }
      }
    }
    if (containerIndex >= 0) {
      const target = path[fragmentIndex + 1];
      event.target = target;
    }
  }
  return event;
}