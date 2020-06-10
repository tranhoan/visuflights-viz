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
        let delay = 2.8
        airports.forEach((airport, index) => {
            let el = airportRef[index].current
            const cx = Math.round(xLowerBound.x + (airport.relativeX*(xUpperBound.x - xLowerBound.x)))
            const cy = Math.round(yUpperBound.y + (airport.relativeY*(yLowerBound.y - yUpperBound.y)+14))
            el.style.transition = `opacity 0.5s ease-in-out ${delay}ms`
            el.style.transform = `translate3d(${cx}px, ${cy}px,0)`
            el.style.opacity = 1;
            delay += 2.8
        })
        
    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound, airportRef])


const ap = airports.map((item,index) => <span ref={airportRef[index]} className="test ap" key={index}></span> )
    return (
        <React.Fragment>
            <div className="map-wrapper">
                <canvas></canvas>
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
