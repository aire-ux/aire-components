import {Component} from '@angular/core';
import {AireButton} from "@aire-ux/aire-button";
import {Aire} from "@aire-ux/aire-theme-manager";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'aire-ux-demo';


  components: any[] = [];

  constructor() {
    this.components.push(AireButton)

    Aire.uninstallStyles();
    Aire.installStyles([{
      id: 'bootstrap',
      url: 'https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css',
      mode: Aire.Mode.Constructable
    }])
    // customElements.define('aire-button', AireButton);
  }

  setMaterial() {
    Aire.uninstallStyles();
    Aire.installStyles([{
      id: 'mdb',
      url: 'https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/3.6.0/mdb.min.css',
      mode: Aire.Mode.Constructable
    }])
  }

  setBootstrap() {
    Aire.uninstallStyles();
    Aire.installStyles([{
      id: 'bootstrap',
      url: 'https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/css/bootstrap.min.css',
      mode: Aire.Mode.Constructable
    }])
  }

}
