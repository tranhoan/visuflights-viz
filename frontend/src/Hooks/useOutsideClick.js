import React, {useEffect } from "react";

/**
 * Hook that alerts clicks outside of the passed ref
 */
const useOutsideClick = (ref) => {
  useEffect(() => {
    /**
     * Alert if clicked on outside of element   
     */
    function handleClickOutside(event) {
      if ((ref && !ref.current.contains(event.target)) && !ref.current.parentElement.contains(event.target)) {
        ref.current.classList.remove('visible')
        ref.current.parentElement.classList.remove('ontop')
      }

    }

    // Bind the event listener
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      // Unbind the event listener on clean up
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [ref]);
}
export default useOutsideClick
