import React, { useState, useEffect, useRef} from 'react';
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
    const airportRef = useRef()

    useTransform(xLowerBound, xLowerRef)
    useTransform(xUpperBound, xUpperRef)
    useTransform(yUpperBound, yUpperRef)
    useTransform(yLowerBound, yLowerRef)
    // useEffect(() => {
    //     axios.get('http://localhost:8080/data')
    //     .then(
    //         res => {
    //             console.log(res)
    //         }
    //     )
    //     .catch(err => console.log(err))
    // }, [])
    useEffect(() => {
        const x = -814.42222
        const y = -409.16111
        let cx = xLowerBound.x + ((x - -1242.5)/ (-688.16667 - -1242.5))* (xUpperBound.x-xLowerBound.x)
        let cy = yUpperBound.y + ((y - -488.0)/ (-245.5 - -488)) * (yLowerBound.y - yUpperBound.y)*1.1
        airportRef.current.style.transform = `translate3d(${cx}px, ${cy}px,0)`
    }, [xLowerBound, xUpperBound, yLowerBound, yUpperBound])

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
                <span ref={airportRef} className="test"></span>
            </div>
        </React.Fragment>
    )
}

export default MainPage
