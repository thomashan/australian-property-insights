package com.thomashan.browser

import com.google.common.net.UrlEscapers
import geb.Configuration
import geb.ConfigurationLoader
import geb.error.NoBaseUrlDefinedException
import geb.url.UrlFragment
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import static com.google.common.net.UrlEscapers.urlFragmentEscaper

class JsoupBrowser {
    public static final String QUERY_STRING_SEPARATOR = "&"

    private final Configuration config
    private JsoupPage jsoupPage

    /**
     * Create a new browser with a default configuration loader, loading the default configuration file.
     *
     * @see geb.ConfigurationLoader
     */
    JsoupBrowser() {
        this(new ConfigurationLoader().conf)
    }

    /**
     * Create a new browser backed by the given configuration.
     *
     * @see geb.Configuration
     */
    JsoupBrowser(Configuration config) {
        this.config = config
    }

    /**
     * Creates a new browser object via the default constructor and executes the closure
     * with the browser instance as the closure's delegate.
     *
     * @return the created browser
     */
    static JsoupBrowser drive(Closure script) {
        drive(new JsoupBrowser(), script)
    }

    /**
     * Executes the closure with browser as its delegate.
     *
     * @return browser
     */
    static JsoupBrowser drive(JsoupBrowser jsoupBrowser, Closure script) {
        script.delegate = jsoupBrowser
        script()
        return jsoupBrowser
    }

    /**
     * Sends the browser to the given page type's url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(JsoupPage)
     * @see JsoupPage#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    <T extends JsoupPage> T to(Map params = [:], Class<T> pageType, Object[] args) {
        return to(params, pageType, null, args)
    }

    /**
     * Sends the browser to the given page type's url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(JsoupPage)
     * @see JsoupPage#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    <T extends JsoupPage> T to(Map params = [:], Class<T> pageType, UrlFragment fragment, Object[] args) {
        to(params, createPage(pageType), fragment, args)
    }

    /**
     * Sends the browser to the given page instance url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(JsoupPage)
     * @see JsoupPage#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    <T extends JsoupPage> T to(Map params = [:], T page, UrlFragment fragment, Object[] args) {
        via(params, page, fragment, args)
        return page
    }

    /**
     * Sends the browser to the given page instance url and sets the page the given instance.
     *
     * @return a page instance that was passed after initializing it.
     * @see #page(JsoupPage)
     * @see JsoupPage#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    <T extends JsoupPage> T via(Map params = [:], T page, UrlFragment fragment, Object[] args) {
        initialisePage(page)
        page.to(params, fragment, args)
        page
    }

    /**
     * Sends the browser to the given url with the given query params and fragment. If the url is relative it is resolved against the {@link #getBaseUrl() base url}.
     */
    void go(Map params = [:], String url = null, UrlFragment fragment = null) {
        URI newUri = calculateUri(url, params, fragment)
        Document document = Jsoup.connect(newUri.toString()).get()
        jsoupPage.document = document
        if (!jsoupPage) {
            page(JsoupPage)
        }
    }

    /**
     * Changes the browser's page to be an instance of the given class.
     * <p>
     * This method performs the following:
     * <ul>
     * <li>Create a new instance of the given class (which must be {@link geb.Page} or a subclass thereof) and connect it to this browser object
     * <li>Inform any registered page change listeners
     * <li>Set the browser's page property to the created instance (if it is not already of this type)
     * </ul>
     * <p>
     * <b>Note that it does not verify that the page matches the current content by running its at checker</b>
     *
     * @return an initialized page instance set as the current page
     */
    <T extends JsoupPage> T page(Class<T> pageClass) {
        T currentPage = createPage(pageClass)
        return (T) makeCurrentPage(currentPage)
    }

    /**
     * Sets this browser's page to be the given page after initializing it.
     *
     * <p>
     * This method performs the following:
     * <ul>
     * <li>Connect the instance passed in to this browser object
     * <li>Inform any registered page change listeners
     * <li>Set the browser's page property to the instance passed in
     * <p>
     * <b>Note that it does not verify that the page matches the current content by running its at checker</b>
     *
     * @return a page instance passed as the first argument after initializing
     */
    <T extends JsoupPage> T page(T page) {
        makeCurrentPage(initialisePage(page))
        page
    }

    /**
     * Sets this browser's page to be the given page, which has already been initialised with this browser instance.
     * <p>
     * Any page change listeners are informed and {@link geb.Page#onUnload(geb.Page)} is called
     * on the previous page and {@link geb.Page#onLoad(geb.Page)} is called on the incoming page.
     * <p>
     */
    private JsoupPage makeCurrentPage(JsoupPage newPage) {
        this.jsoupPage = newPage
    }

    private URI calculateUri(String path, Map params, UrlFragment fragment) {
        URI absolute = calculateAbsoluteUri(path)
        String effectiveFragment = urlFragmentEscaper().escape(fragment?.toString() ?: "") ?: absolute.rawFragment

        if (absolute.opaque) {
            new URI(absolute.scheme, absolute.schemeSpecificPart, effectiveFragment)
        } else {
            def uriStringBuilder = new StringBuilder() << new URI(
                absolute.scheme, absolute.userInfo, absolute.host, absolute.port, absolute.path, null, null
            )

            def queryString = [absolute.rawQuery, toQueryString(params)].findAll().join(QUERY_STRING_SEPARATOR) ?: null
            if (queryString) {
                uriStringBuilder << "?" << queryString
            }

            if (effectiveFragment) {
                uriStringBuilder << "#" << effectiveFragment
            }

            new URI(uriStringBuilder.toString())
        }
    }

    private static String toQueryString(Map params) {
        def escaper = UrlEscapers.urlFormParameterEscaper()
        if (params) {
            params.collectMany { name, value ->
                def values = value instanceof Collection ? value : [value]
                values.collect { v ->
                    "${escaper.escape(name.toString())}=${escaper.escape(v.toString())}"
                }
            }.join(QUERY_STRING_SEPARATOR)
        } else {
            null
        }
    }

    private URI calculateAbsoluteUri(String path) {
        def absolute
        if (path) {
            absolute = new URI(path)
            if (!absolute.absolute) {
                absolute = new URI(getBaseUrlRequired()).resolve(absolute)
            }
        } else {
            absolute = new URI(getBaseUrlRequired())
        }
        return absolute
    }

    private String getBaseUrlRequired() {
        String baseUrl = getBaseUrl()
        if (baseUrl == null) {
            throw new NoBaseUrlDefinedException()
        }
        return baseUrl
    }

    /**
     * The url to resolve all relative urls against. Typically the root of the application or system
     * Geb is interacting with.
     * <p>
     * The base url is determined by the configuration.
     *
     * @see geb.Configuration#getBaseUrl()
     */
    String getBaseUrl() {
        config.baseUrl
    }

    /**
     * Creates a new instance of the given page type and initialises it.
     *
     * @return The newly created page instance
     */
    <T extends JsoupPage> T createPage(Class<T> pageType) {
        validatePage(pageType)
        T instance = (T) pageType.constructors[0].newInstance()
        initialisePage(instance)
        return instance
    }

    private static void validatePage(Class<?> pageType) {
        if (!JsoupPage.isAssignableFrom(pageType)) {
            throw new IllegalArgumentException("$pageType is not a subclass of ${JsoupPage}")
        }
    }

    private <T extends JsoupPage> T initialisePage(T page) {
        if (!this.is(page.jsoupBrowser)) {
            page.init(this)
        }
        return page
    }
}
