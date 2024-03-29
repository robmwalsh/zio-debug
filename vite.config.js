import {resolve} from 'path'
import {minifyHtml, injectHtml} from 'vite-plugin-html'

const scalaVersion = '3.1.1'

// https://vitejs.dev/config/
export default ({mode}) => {
    const mainJS = `./modules/frontend/target/scala-${scalaVersion}/frontend-${mode === 'production' ? 'opt' : 'fastopt'}/main.js`
    const script = `<script type="module" src="${mainJS}"></script>`

    return {
        server: {
            proxy: {
                '/api': {
                    target: 'http://localhost:8088',
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, '')
                },
            }
        },
        publicDir: './src/main/static/public',
        plugins: [
            ...(process.env.NODE_ENV === 'production' ? [minifyHtml(),] : []),
            injectHtml({
                injectData: {
                    script
                }
            })
        ],
        resolve: {
            alias: {
                'stylesheets': resolve(__dirname, './modules/frontend/src/main/static/stylesheets'),
            }
        }
    }
}