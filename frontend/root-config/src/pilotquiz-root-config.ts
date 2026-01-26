import { registerApplication, start } from "single-spa";
import {
  constructApplications,
  constructRoutes,
  constructLayoutEngine,
} from "single-spa-layout";
import microfrontendLayout from "./microfrontend-layout.html";

// Declare System global for TypeScript
declare const System: {
  import: (name: string) => Promise<any>;
};

const routes = constructRoutes(microfrontendLayout, {
  loaders: {},
  props: {
    navbarProps: { type: 'navbar' }
  }
});
const applications = constructApplications({
  routes,
  loadApp({ name }) {
    // Use SystemJS to load all modules
    if (name === '@pilotquiz/navbar') {
      return System.import('@pilotquiz/react-auth');
    }
    return System.import(name);
  },
});
const layoutEngine = constructLayoutEngine({ routes, applications });

applications.forEach(registerApplication);
layoutEngine.activate();
start();
