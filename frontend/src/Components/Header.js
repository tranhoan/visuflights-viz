import React from 'react'
import Logo from '../Images/logo.svg';

function Header() {
    return (
        <header>
            <img src={Logo} className="logo" />
            <div className="right-column">
                <div className="map-menu menu-item">
                    <i className="far fa-map"></i>
                    <div> Map </div>
                </div>
                <div className="menu-item"> About </div>
                <div className="menu-item"> Contact </div>
            </div>
        </header>
    )
}

export default Header
