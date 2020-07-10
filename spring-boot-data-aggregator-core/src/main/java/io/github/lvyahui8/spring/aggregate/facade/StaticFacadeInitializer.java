package io.github.lvyahui8.spring.aggregate.facade;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2020/2/13
 */
public class StaticFacadeInitializer {
    public static void initFacade(DataAggregateQueryFacade facade) {
        if (DataQueryStaticFacade.getFacade() != null) {
            throw new UnsupportedOperationException("DataFacade can be initialized only once.");
        }
        DataQueryStaticFacade.setFacade(facade);
    }
}
