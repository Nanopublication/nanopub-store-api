/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.lumc.nanopub.store.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import nl.lumc.nanopub.store.api.json.ResponseWrapper;
import nl.lumc.nanopub.store.dao.NanopubDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Reinout, Rajaram, Eelke, Mark
 * @since 25-10-2013
 * @version 0.2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:test-api-context.xml")
public class NanopubControllerTest {

//    Not used in standalone setup
//    @Inject
//    private WebApplicationContext wac;
    @Inject
    private NanopubController controller;

    private NanopubDao nanopubDao;

    private MockMvc mockMvc;

    private static final org.slf4j.Logger logger
            = LoggerFactory.getLogger(NanopubController.class);

    @Before
    public void setup() {
        //mockMvc = webAppContextSetup(this.wac).build();
        mockMvc = standaloneSetup(controller).build();
        nanopubDao = mock(NanopubDao.class);

        List<URI> uris = null;

        try {
            uris = Collections.
                    singletonList(new URI("http://mydomain.com/nanopubs/1"));
        } catch (URISyntaxException ex) {
            logger.warn("Cannot setup test environment!", ex);
        }

        when(nanopubDao.listNanopubs()).thenReturn(uris);
        controller.setNanopubDao(nanopubDao);
    }

    @Test
    public void testStoreNanopubURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs/1")).andExpect(status().isOk()); //.param("np", "bla bla")); // /nanopubs/?np=bla%20bla
    }

    @Test
    public void testStoreNanopubURLMappingInvalid() throws Exception {
        mockMvc.perform(get("/nanopub_invalid_url"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testStoreNanopubResponse() {

        String nanopub = "bla bla";
        String contentType = "application/xtrig";
        ResponseWrapper expected = new ResponseWrapper();
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        expected.setValue("Thanks for " + nanopub + " of type " + contentType);
        ResponseWrapper actual = controller.storeNanopub(contentType, nanopub, httpResponse);
        Assert.assertEquals(expected.getValue(), actual.getValue());
    }

    @Test
    public void testStoreNanopubResponseLegalContentType() {
        String nanopub = "bla bla";
        String contentType = "application/x-trig";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        controller.storeNanopub(contentType, nanopub, httpResponse);

        Assert.assertEquals(httpResponse.getStatus(), HttpServletResponse.SC_OK);
    }

    @Test
    public void testStoreNanopubResponseIllegalContentType() {
        String nanopub = "bla bla";
        String contentType = "application/unsupportedMimeType";
        MockHttpServletResponse httpResponse = new MockHttpServletResponse();

        controller.storeNanopub(contentType, nanopub, httpResponse);

        Assert.assertEquals(httpResponse.getStatus(), HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    public void testRetrieveNanopubsListURLMapping() throws Exception {
        mockMvc.perform(get("/nanopubs")).andDo(print());
    }

    @Test
    public void testRetrieveNanopubsList() throws Exception {
        List<URI> result = controller.listNanopubs();
        assertNotNull(result);
    }

}
