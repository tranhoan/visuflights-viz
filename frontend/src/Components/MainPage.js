import React, { useState, useEffect, useRef, createRef} from 'react';
import axios from 'axios';
import Map from './Map';
import useTransform from '../Hooks/useTransform';

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
        // const x = -934
        // const y = -485.66667
        // let cx = xLowerBound.x + (((x - -1242.5)/ (-688.16667 - -1242.5))* (xUpperBound.x-xLowerBound.x))
        // let cy = yUpperBound.y + ((y - -488.0)/ (-245.5 - -488)) * (yLowerBound.y - yUpperBound.y)
        // airportRef.current.style.transform = `translate3d(${cx}px, ${cy}px,0)`
        console.log(airportRef)
        let delay = 2.8
        airports.forEach((airport, index) => {
            // if (index==0 || airports[0].departures[0].points[airports[0].departures[0].points.length-1].x == airport.x) {
            let el = airportRef[index].current
            console.log(index)
            const cx = calculateViewportX(airport.relativeX)
            const cy = calculateViewportY(airport.relativeY)
            el.style.transition = `opacity 0.5s ease-in-out ${delay}ms`
            el.style.transform = `translate3d(${cx}px, ${cy}px,0)`
            el.style.opacity = 1;
            delay += 2.8
            // }
        })
        
    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound, airportRef])


    useEffect(() => {
        //0, 136
        canvas.current.height = window.innerHeight
        canvas.current.width = window.innerWidth
        if (airportRef.length != 0) {
        let c = canvas.current.getContext('2d')
        let p1 = airportRef[0].current.getBoundingClientRect()
        let p2 = airportRef[136].current.getBoundingClientRect()
        // bzCurve(airports[0].departures[0].points, 0.3, 1, c)
        airports.forEach((a) => {
            a.departures.map((d) => bzCurve(d.points, 0.5, 1, c))
        })
    }

    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound, airportRef])

    const calculateViewportX = (rx) => {
        const newX = Math.round(xLowerBound.x + (rx*(xUpperBound.x - xLowerBound.x)))
        return newX
    }

    const calculateViewportY = (ry) => {
        const newY = Math.round(yUpperBound.y + (ry*(yLowerBound.y - yUpperBound.y)+14))
        return newY
    }

    const gradient = (a,b) => {
        return (calculateViewportY(b.relativeY)-calculateViewportY(a.relativeY))/(calculateViewportX(b.relativeX)-calculateViewportX(a.relativeX)); 
    }

    const bzCurve = (points, f, t, ctx) => {
        if (typeof(f) == 'undefined') f = 0.3; 
        if (typeof(t) == 'undefined') t = 0.6; 
        console.log(points)
        ctx.beginPath(); 
        ctx.moveTo(calculateViewportX(points[0].relativeX), calculateViewportY(points[0].relativeY)); 

        let m = 0; 
        let dx1 = 0; 
        let dy1 = 0; 
        var preP = points[0]; 
        console.log(points)
        for (var i = 1; i < points.length-1; i++) { 
            let curP = points[i]; 
            let nexP = points[i + 1];
            const curx = calculateViewportX(curP.relativeX)
            const cury = calculateViewportY(curP.relativeY)
            const nexx = calculateViewportX(nexP.relativeX)
            const nexy = calculateViewportY(nexP.relativeY)
            const prepx = calculateViewportX(preP.relativeX)
            const prepy = calculateViewportY(preP.relativeY)
            let dx2 = 0
            let dy2 = 0
            if (nexP) { 
                m = gradient(preP, nexP); 
                dx2 = (nexx - curx) *-f;
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
        ctx.stroke(); 
    } 


const ap = airports.map((item,index) => <span ref={airportRef[index]} className="test ap" key={index}></span> )
    return (
        <React.Fragment>
            <div className="map-wrapper">
                <canvas ref={canvas}></canvas>
                <Map setPosition={position => setXLowerBound(position)} 
                setXUpper={position => setXUpperBound(position)}
                setYUpper={position => setYUpperBound(position)}
                setYLower={position => setYLowerBound(position)}/>
                {/* <img ref={mapRef} src={USAimage} className="svg" /> */}
                <span ref={xLowerRef} className="test"></span>
                <span ref={xUpperRef} className="test"></span>
                <span ref={yUpperRef} className="test"></span>
                <span ref={yLowerRef} className="test"/>
                {ap}
            </div>
        </React.Fragment>
    )
}

export default MainPage
