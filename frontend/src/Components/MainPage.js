import React, { useState, useEffect, useRef, createRef } from 'react';
import axios from 'axios';
import Map from './Map';
import useTransform from '../Hooks/useTransform'
import Point from './Point';

function MainPage() {
    let [xLowerBound, setXLowerBound] = useState(0)
    let [xUpperBound, setXUpperBound] = useState(0)
    let [yUpperBound, setYUpperBound] = useState(0)
    let [yLowerBound, setYLowerBound] = useState(0)
    let [airports, setAirports] = useState([])
    const xLowerRef = useRef()
    const xUpperRef = useRef()
    const yUpperRef = useRef()
    const yLowerRef = useRef()
    const mapRef = useRef()
    const [airportRef, setAirportRef] = useState([])
    const canvas = useRef()

    useTransform(xLowerBound, xLowerRef)
    useTransform(xUpperBound, xUpperRef)
    useTransform(yUpperBound, yUpperRef)
    useTransform(yLowerBound, yLowerRef)

    useEffect(() => {
        axios.get('http://localhost:8080/data')
            .then(
                res => {
                    console.log(res)
                    setAirports(res.data.airports)
                }
            )
            .catch(err => console.log(err))
    }, [])

    useEffect(() => {
        setAirportRef(oldRef => (
            Array(airports.length).fill().map((e, i) => oldRef[i] || createRef())
        ))
    }, [airports])

    useEffect(() => {
        let delay = 2.8
        let levels = { level1: 0, level2: 0, level3: 0, level4: 0, level5: 0, level6: 0 }
        airports.forEach((airport, index) => {
            // if (index==0 || airports[0].departures[0].points[airports[0].departures[0].points.length-1].x == airport.x) {
            let el = airportRef[index].current
            let scale = getAirportLevel(airport.airportSize)
            el.style.transition = `opacity 0.5s ease-in-out ${delay}ms, z-index 0.5s ease-in-out 1ms, width 0.3s ease-in-out ${delay}ms, height 0.3s ease-in-out ${delay}ms`
            el.style.width = `${scale}rem`
            el.style.height = `${scale}rem`
            let wh = parseInt(getComputedStyle(document.documentElement).fontSize)*scale + 6
            console.log(wh/2)
            console.log(el.offsetWidth)
            if(scale < 3) {
                el.style.zIndex = 8;
            }
            const cx = calculateViewportX(airport.relativeX) - (wh / 2)
            const cy = calculateViewportY(airport.relativeY) - (wh / 2)
            el.style.transform = `translate3d(${cx}px, ${cy}px,0)`
            el.style.opacity = 1;
            delay += 2.8
            // }
        })
        console.log(levels)
    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound, airportRef])


    useEffect(() => {
        canvas.current.height = window.innerHeight
        canvas.current.width = window.innerWidth
        if (airportRef.length != 0) {
            let c = canvas.current.getContext('2d')
            let p1 = airportRef[0].current.getBoundingClientRect()
            let p2 = airportRef[136].current.getBoundingClientRect()
            // bzCurve(airports[0].departures[0].points, 0, 0, c)
            // let start = null
            // Clear canvas
            // c.clearRect(0, 0, c.canvas.width, c.canvas.height);
            airports.forEach((a) => {
                a.departures.map((d) => {
                    bzCurve(d.points, 0, 0, c)
                })
            })

            // airports.forEach((a, ai) => {
            //         a.departures.map((d,di) => { 
            //             if (d.points[d.points.length-1].y==-474.48889) {
            //                 console.log(a)
            //                 // drawPoints(d.points, c)
            //                 drawLine(d.points, c)
            //                 // animatePathDrawing(c,d)
            //             }
            //         })
            // })
        }

    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound, airportRef])

    const getAirportLevel = (size) => {
        let level = 0
        if (size <= 5) {
            level = 1
        } else if (size > 5 && size <= 15) {
            level = 1.5
        } else if (size > 15 && size <= 50) {
            level = 2
        } else if (size > 50 && size <=100) {
            level = 3
        } else if (size > 100 && size <= 150) {
            level = 3.2
        }
        else {
            level = 4
        }
        return level
    }

    const calculateViewportX = (rx) => {
        const newX = Math.round(xLowerBound.x + (rx * (xUpperBound.x - xLowerBound.x)))
        return newX
    }

    const calculateViewportY = (ry) => {
        const newY = Math.round(yUpperBound.y + (ry * (yLowerBound.y - yUpperBound.y) + 14))
        return newY
    }

    const gradient = (a, b) => {
        return (calculateViewportY(b.relativeY) - calculateViewportY(a.relativeY)) / (calculateViewportX(b.relativeX) - calculateViewportX(a.relativeX));
    }

    const bzCurve = (points, f, t, ctx) => {
        if (typeof (f) == 'undefined') f = 0.3;
        if (typeof (t) == 'undefined') t = 0.6;
        ctx.beginPath();
        ctx.moveTo(calculateViewportX(points[0].relativeX), calculateViewportY(points[0].relativeY));
        let m = 0;
        let dx1 = 0;
        let dy1 = 0;
        var preP = points[0];
        for (var i = 1; i < points.length; i++) {
            let curP = points[i];
            let nexP;
            const curx = calculateViewportX(curP.relativeX)
            const cury = calculateViewportY(curP.relativeY)
            const prepx = calculateViewportX(preP.relativeX)
            const prepy = calculateViewportY(preP.relativeY)
            let dx2 = 0
            let dy2 = 0
            if (i < points.length - 1) {
                nexP = points[i + 1];
                m = gradient(preP, nexP);
                const nexx = calculateViewportX(nexP.relativeX)
                dx2 = (nexx - curx) * -f;
                dy2 = dx2 * m * t;
            } else {
                dx2 = 0;
                dy2 = 0;
            }

            ctx.bezierCurveTo(
                prepx - dx1, prepy - dy1,
                curx + dx2, cury + dy2,
                curx, cury
            );

            dx1 = dx2;
            dy1 = dy2;
            preP = curP;
        }
        ctx.strokeStyle = "#FFBC65";
        ctx.globalAlpha = 0.2
        ctx.lineWidth = 1.5
        ctx.stroke();
        ctx.closePath();
    }

    const ap = airports.map((item, index) => (
        <Point key={index} ref={airportRef[index]} latitude={(-airports[index].y / 10).toFixed(5)} longtitude={(airports[index].x / 10).toFixed(5)} abbr={airports[index].abbreviation} />
    ))
    return (
        <React.Fragment>
            <div className="map-wrapper" ref={mapRef}>
                <canvas ref={canvas}></canvas>
                <Map setPosition={position => setXLowerBound(position)}
                    setXUpper={position => setXUpperBound(position)}
                    setYUpper={position => setYUpperBound(position)}
                    setYLower={position => setYLowerBound(position)} />
                {/* <img ref={mapRef} src={USAimage} className="svg" /> */}
                <span ref={xLowerRef} className="test"></span>
                <span ref={xUpperRef} className="test"></span>
                <span ref={yUpperRef} className="test"></span>
                <span ref={yLowerRef} className="test" />
                {ap}
            </div>
        </React.Fragment>
    )
}

export default MainPage
