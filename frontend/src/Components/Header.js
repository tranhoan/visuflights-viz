import React from 'react'
import Logo from '../Images/logo.svg';

function Header() {
    return (
        <header>
            <img src={Logo} className="logo" />
            <div className="right-column">
                <div> About </div>
                <div> Contact </div>
            </div>
        </header>
    )
}

export default Header
