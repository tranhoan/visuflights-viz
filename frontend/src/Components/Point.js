import React, { forwardRef, useRef } from 'react'
import useOutsideClick from '../Hooks/useOutsideClick';

const Point = forwardRef(function Point({latitude, longtitude, abbr, isActive, i, changeActive, len}, airportRef) {
    const tooltipRef = useRef()
    const handleClick = () => {
        airportRef.current.lastChild.classList.toggle('visible')
        airportRef.current.classList.toggle('ontop')
    }

    const handleHover = () => {
        let n = Array(len).fill(false)
        n[i] = true
        changeActive(n, i, true)
    }

    const handleHoverLeave = () => {
        let n = Array(len).fill(true)
        changeActive(n, i, false)
    }

    useOutsideClick(tooltipRef)

    return (
    <div className="point-wrapper">
        <div ref={airportRef} className={`test ap ${isActive==true ? 'active' : (isActive===-1 ? '' : 'not-active')}`} onClick={() => handleClick()} onMouseOver={() => handleHover()} onMouseLeave={() => handleHoverLeave()}>
            <div className="tooltip" ref={tooltipRef}>
                <div className="tooltip-description">
                    <span className="desc">LONGTITUDE</span>
                    <span className="val">{longtitude}</span>
                </div>
                <div className="tooltip-description">
                    <span className="desc">LATITUDE</span>
                    <span className="val">{latitude}</span>
                </div>
                <div className="tooltip-name">
                    {abbr}
                </div>
            </div>
        </div>
    </div>
    )
});

export default Point
