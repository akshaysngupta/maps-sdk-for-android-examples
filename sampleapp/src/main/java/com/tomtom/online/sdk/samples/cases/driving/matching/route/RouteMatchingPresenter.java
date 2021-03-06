/**
 * Copyright (c) 2015-2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be used
 * for internal evaluation purposes or commercial use strictly subject to separate licensee
 * agreement between you and TomTom. If you are the licensee, you are only permitted to use
 * this Software in accordance with the terms of your license agreement. If you are not the
 * licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */
package com.tomtom.online.sdk.samples.cases.driving.matching.route;

import com.google.common.base.Preconditions;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.driving.LatLngTraceMatchingDataProvider;
import com.tomtom.online.sdk.map.driving.Matcher;
import com.tomtom.online.sdk.map.driving.MatcherFactory;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.samples.activities.FunctionalExampleModel;
import com.tomtom.online.sdk.samples.cases.driving.AbstractRoutingPresenter;
import com.tomtom.online.sdk.samples.cases.driving.ChevronMatcherUpdater;
import com.tomtom.online.sdk.samples.cases.driving.utils.BaseSimulator;
import com.tomtom.online.sdk.samples.cases.driving.utils.RouteSimulator;
import com.tomtom.online.sdk.samples.cases.driving.utils.interpolator.RandomizeInterpolator;

public class RouteMatchingPresenter extends AbstractRoutingPresenter {

    private Matcher matcher;

    @Override
    protected void onPresenterRestored() {
        super.onPresenterRestored();
        restoreRouteOverview();
        createMatcher();
    }

    @Override
    protected void onPresenterReady() {
        super.onPresenterReady();
        blockZoomGestures();
        startTracking();
    }

    @Override
    public void cleanup() {
        if (matcher != null) {
            //Matcher may be null if routing request failed
            matcher.dispose();
        }
        super.cleanup();
    }

    @Override
    protected BaseSimulator createSimulator() {
        //Route is stored within Maps SDK
        return new RouteSimulator(getRoutesFromMap(), new RandomizeInterpolator());
    }

    @Override
    protected LatLng getDefaultMapPosition() {
        return ROUTE_CONFIG.getOrigin();
    }

    @Override
    protected BaseSimulator.SimulatorCallback getSimulatorCallback() {
        return simulatorCallback;
    }

    @Override
    public FunctionalExampleModel getModel() {
        return new RouteMatchingFunctionalExample();
    }

    @Override
    protected void onRouteReady(FullRoute route) {
        super.onRouteReady(route);
        createMatcher();
    }

    private void createMatcher() {
        Preconditions.checkArgument(tomtomMap.getRoutes().size() > 0);
        //tag::doc_create_route_matcher[]
        matcher = MatcherFactory.createMatcher(LatLngTraceMatchingDataProvider.fromPoints(getFirstRouteFromMap()));
        matcher.setMatcherListener(new ChevronMatcherUpdater(getChevron(), tomtomMap));
        //end::doc_create_route_matcher[]
    }

    private BaseSimulator.SimulatorCallback simulatorCallback = location -> matcher.match(location);

}