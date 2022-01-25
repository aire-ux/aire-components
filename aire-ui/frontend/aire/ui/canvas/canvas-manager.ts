
export interface CanvasAwareWindow extends Window {
  getCanvas(id: string) : {setId(id: string) : void};
}

declare let window: CanvasAwareWindow;

window.getCanvas = (id: string) => {
  return new class {
    setId(otherId: any) : void {
      alert("ID " + id + " otherId" + otherId);
    }
  }
}