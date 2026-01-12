import { enableProdMode, NgZone, importProvidersFrom } from '@angular/core';
import { Router, NavigationStart, provideRouter } from '@angular/router';
import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { singleSpaAngular, getSingleSpaExtraProviders } from 'single-spa-angular';

import { App } from './app/app';
import { routes } from './app/app.routes';
import { environment } from './environments/environment';
import { singleSpaPropsSubject } from './single-spa/single-spa-props';

if (environment.production) {
  enableProdMode();
}

const lifecycles = singleSpaAngular({
  bootstrapFunction: singleSpaProps => {
    singleSpaPropsSubject.next(singleSpaProps);
    return bootstrapApplication(App, {
      providers: [
        getSingleSpaExtraProviders(),
        provideRouter(routes),
        provideHttpClient(withInterceptorsFromDi())
      ]
    });
  },
  template: '<app-root />',
  Router,
  NavigationStart,
  NgZone,
});

export const bootstrap = lifecycles.bootstrap;
export const mount = lifecycles.mount;
export const unmount = lifecycles.unmount;
