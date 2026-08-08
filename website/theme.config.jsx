import { useConfig } from 'nextra-theme-docs'
import { useRouter } from 'next/router'

const Logo = () => (
  <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 700 }}>
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" aria-hidden="true">
      <path
        d="M12 2a10 10 0 1 0 0 20 10 10 0 0 0 0-20Zm0 5.5a4.5 4.5 0 0 1 4.36 5.625H12V11h2.06a2.5 2.5 0 1 0-.56 2.625H16.5A4.5 4.5 0 1 1 12 7.5Z"
        fill="#FD366E"
      />
    </svg>
    <span>Appwrite KMP</span>
  </span>
)

export default {
  logo: <Logo />,
  project: {
    link: 'https://github.com/AndroidPoet/appwrite-kmp',
  },
  docsRepositoryBase: 'https://github.com/AndroidPoet/appwrite-kmp/tree/main/website',
  color: {
    hue: 340,
    saturation: 98,
  },
  footer: {
    content: (
      <span>
        MIT © {new Date().getFullYear()}{' '}
        <a href="https://github.com/AndroidPoet/appwrite-kmp" target="_blank" rel="noreferrer">
          Appwrite KMP
        </a>
        . A Kotlin Multiplatform SDK for Appwrite.
      </span>
    ),
  },
  head: function useHead() {
    const { frontMatter } = useConfig()
    const { asPath } = useRouter()
    const pageTitle = frontMatter?.title
    const title = pageTitle ? `${pageTitle} – Appwrite KMP` : 'Appwrite KMP'
    const description =
      frontMatter?.description ??
      'Appwrite KMP — a Kotlin Multiplatform SDK for Appwrite, built from scratch with errors-as-values, typed IDs, a query DSL and Flow-based realtime across Android, iOS, JVM and Wasm.'
    const base = 'https://androidpoet.github.io/appwrite-kmp'
    const path = asPath === '/' ? '' : asPath.split('?')[0].split('#')[0]
    const canonical = `${base}${path}`
    const ogImage = `${base}/favicon.svg`
    return (
      <>
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <title>{title}</title>
        <meta name="description" content={description} />
        <link rel="canonical" href={canonical} />
        <link rel="icon" href={`${base}/favicon.svg`} type="image/svg+xml" />
        <meta name="theme-color" content="#FD366E" />
        <meta property="og:type" content="website" />
        <meta property="og:site_name" content="Appwrite KMP" />
        <meta property="og:url" content={canonical} />
        <meta property="og:title" content={pageTitle ?? 'Appwrite KMP'} />
        <meta property="og:description" content={description} />
        <meta property="og:image" content={ogImage} />
        <meta name="twitter:card" content="summary_large_image" />
        <meta name="twitter:title" content={pageTitle ?? 'Appwrite KMP'} />
        <meta name="twitter:description" content={description} />
        <meta name="twitter:image" content={ogImage} />
      </>
    )
  },
  sidebar: {
    defaultMenuCollapseLevel: 1,
  },
  toc: {
    backToTop: true,
  },
  navigation: {
    prev: true,
    next: true,
  },
  darkMode: true,
}
