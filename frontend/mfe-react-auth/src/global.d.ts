// Global type declarations for the React MFE

// CSS module declarations - allows importing CSS files
declare module '*.css' {
    const classes: { readonly [key: string]: string };
    export default classes;
}

declare module '*.scss' {
    const classes: { readonly [key: string]: string };
    export default classes;
}

declare module '*.sass' {
    const classes: { readonly [key: string]: string };
    export default classes;
}

// Image assets
declare module '*.png' {
    const value: string;
    export default value;
}

declare module '*.jpg' {
    const value: string;
    export default value;
}

declare module '*.jpeg' {
    const value: string;
    export default value;
}

declare module '*.gif' {
    const value: string;
    export default value;
}

declare module '*.svg' {
    const value: string;
    export default value;
}

declare module '*.webp' {
    const value: string;
    export default value;
}

// HTML templates
declare module '*.html' {
    const value: string;
    export = value;
}
